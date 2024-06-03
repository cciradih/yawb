package org.eu.cciradih.yawb.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.state.PollingWechatState;
import org.eu.cciradih.yawb.component.state.UnauthenticatedWechatState;
import org.eu.cciradih.yawb.data.WeChatContactTransfer;
import org.eu.cciradih.yawb.data.WeChatMsgTransfer;
import org.eu.cciradih.yawb.data.WeChatSyncKeyTransfer;
import org.eu.cciradih.yawb.data.WeChatTransfer;
import org.eu.cciradih.yawb.enumeration.CacheEnum;
import org.eu.cciradih.yawb.enumeration.CodeEnum;
import org.eu.cciradih.yawb.enumeration.MsgTypeEnum;
import org.eu.cciradih.yawb.interceptor.BotInterceptorHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Getter
@Component
public class WechatContext {
    private final WeChatClientComponent weChatClientComponent;
    private final CacheComponent cacheComponent;
    private final ObjectMapper objectMapper;
    private final ConfigurableApplicationContext configurableApplicationContext;
    private final BotInterceptorHandler botInterceptorHandler;

    private String uuid;
    private String redirectUri;

    private WeChatTransfer weChatTransfer;
    private Map<String, WeChatContactTransfer> weChatContactTransferMap;

    private WechatState wechatState;

    public WechatContext(WeChatClientComponent weChatClientComponent, CacheComponent cacheComponent, ObjectMapper objectMapper, ConfigurableApplicationContext configurableApplicationContext, BotInterceptorHandler botInterceptorHandler) {
        this.weChatClientComponent = weChatClientComponent;
        this.cacheComponent = cacheComponent;
        this.objectMapper = objectMapper;
        this.configurableApplicationContext = configurableApplicationContext;
        this.botInterceptorHandler = botInterceptorHandler;
        WeChatTransfer webWxNewLoginPage = cacheComponent.get(CacheEnum.BASE_REQUEST, WeChatTransfer.class);
        if (webWxNewLoginPage == null) {
            this.wechatState = new UnauthenticatedWechatState(this);
        } else {
            this.weChatTransfer = webWxNewLoginPage;
            this.wechatState = new PollingWechatState(this);
        }
    }

    public void setWechatState(WechatState wechatState) {
        this.wechatState = wechatState;
        this.wechatState.handle();
    }

    public void getQRCode() {
        WeChatTransfer jsLogin = this.weChatClientComponent.getJsLogin();
        this.uuid = jsLogin.getUuid();
        log.info("wechat.uuid: {}", this.uuid );
        String qrCodeUri = this.weChatClientComponent.getQrCodeUri(this.uuid );
        log.info("QR Code Uri: {}", qrCodeUri);
    }

    public boolean login() {
        while (true) {
            WeChatTransfer weChatTransfer = this.weChatClientComponent.getLoginUri(uuid);
            redirectUri = weChatTransfer.getRedirectUri();
            String code = weChatTransfer.getCode();
            CodeEnum codeEnum = CodeEnum.getByName(code);
            switch (codeEnum) {
                case S_AWAIT -> log.info("Scanned, waiting to continue logging in.");
                case S_SUCCESS -> {
                    return true;
                }
                case S_TIMEOUT -> getQRCode();
            }
            try {
                Thread.sleep(100); // Poll every 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return false;
    }

    public void initPage() {
        log.info("Logged in, starting initialization.");
        WeChatTransfer webWxNewLoginPage = this.weChatClientComponent.getWebWxNewLoginPage(redirectUri);

        WeChatTransfer webWxInit = this.weChatClientComponent.postWebWxInit(webWxNewLoginPage);
        WeChatSyncKeyTransfer weChatSyncKeyTransfer = webWxInit.getSyncKey();
        webWxNewLoginPage.setSyncKey(weChatSyncKeyTransfer);

        this.weChatTransfer = webWxNewLoginPage;

        this.weChatTransfer.setUuid(this.uuid);
        this.weChatTransfer.setRedirectUri(redirectUri);
        if(this.weChatTransfer.getDeviceId()  == null){
            long time = new Date().getTime();
            this.weChatTransfer.setDeviceId("e" + String.valueOf(time).repeat(2).substring(0, 15));
        }
        this.cacheComponent.put(CacheEnum.BASE_REQUEST, webWxNewLoginPage);

        WeChatTransfer webWxGetContact = this.weChatClientComponent.postWebWxGetContact(webWxNewLoginPage);

        Map<String, WeChatContactTransfer> weChatContactTransferMap = webWxGetContact.getMemberList()
                .stream()
                .collect(Collectors.toMap(WeChatContactTransfer::getUserName, Function.identity(), (first, second) -> first));
        WeChatContactTransfer user = webWxInit.getUser();
        weChatContactTransferMap.put(user.getUserName(), user);

        log.info("weChatContactTransferMap:{}.", weChatContactTransferMap);
        this.weChatContactTransferMap = weChatContactTransferMap;

        this.cacheComponent.put(CacheEnum.USER, user);
        this.cacheComponent.put(CacheEnum.WE_CHAT_CONTACT_TRANSFER_MAP, weChatContactTransferMap);

        log.info("The initialization is completed and synchronization of messages begins.");
    }


    public int pollForMessages() {
        WeChatTransfer syncCheck = this.weChatClientComponent.getSyncCheck(weChatTransfer);
        String retCode = syncCheck.getRetCode();
        CodeEnum codeEnum = CodeEnum.getByName(retCode);
        switch (codeEnum) {
            case X_SUCCESS:
                String selector = syncCheck.getSelector();
                codeEnum = CodeEnum.getByName(selector);
                if (codeEnum == CodeEnum.HAS_MSG) {
                    WeChatTransfer webWxSync = this.weChatClientComponent.postWebWxSync(this.weChatTransfer);
                    List<WeChatMsgTransfer> webWxSyncAddMsgList = webWxSync.getAddMsgList();
                    if (!CollectionUtils.isEmpty(webWxSyncAddMsgList)) {
                        webWxSyncAddMsgList.forEach(weChatMsgTransfer -> {
                            Integer msgType = weChatMsgTransfer.getMsgType();
                            MsgTypeEnum msgTypeEnum = MsgTypeEnum.getByName(msgType);
                            String fromUserName = weChatMsgTransfer.getFromUserName();
                            String toUserName = weChatMsgTransfer.getToUserName();
                            String content = weChatMsgTransfer.getContent();
                            this.botInterceptorHandler.send(msgTypeEnum, fromUserName, toUserName, content);
                        });
                    }
                }
                break;
            case NOT_LOGIN_WARN:
            case NOT_LOGIN_CHECK:
                return -1;
            default:
                log.error("Ret Code: {}", retCode);
                Thread.currentThread().interrupt();
                if (Thread.currentThread().isInterrupted()) {
                    this.configurableApplicationContext.close();
                }
                break;
        }
        return 0; // 模拟无错误，继续轮询
    }
}
