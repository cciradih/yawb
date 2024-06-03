package org.eu.cciradih.yawb.component.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.CacheComponent;
import org.eu.cciradih.yawb.component.WeChatClientComponent;
import org.eu.cciradih.yawb.data.WeChatContactTransfer;
import org.eu.cciradih.yawb.data.WeChatMsgTransfer;
import org.eu.cciradih.yawb.data.WeChatSyncKeyTransfer;
import org.eu.cciradih.yawb.data.WeChatTransfer;
import org.eu.cciradih.yawb.enumeration.CacheEnum;
import org.eu.cciradih.yawb.enumeration.CodeEnum;
import org.eu.cciradih.yawb.enumeration.MsgTypeEnum;
import org.eu.cciradih.yawb.interceptor.BotInterceptorHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class SyncCheckTask implements Runnable {
    private final WeChatClientComponent weChatClientComponent;
    private final ConfigurableApplicationContext configurableApplicationContext;
    private WeChatTransfer weChatTransfer;
    private Map<String, WeChatContactTransfer> weChatContactTransferMap;
    private final BotInterceptorHandler botInterceptorHandler;
    private final CacheComponent cacheComponent;

    @Override
    public void run() {
        WeChatTransfer syncCheck = this.weChatClientComponent.getSyncCheck(weChatTransfer);
        String retCode = syncCheck.getRetCode();
        CodeEnum codeEnum = CodeEnum.getByName(retCode);
        if (codeEnum == CodeEnum.X_SUCCESS) {
            String selector = syncCheck.getSelector();
            codeEnum = CodeEnum.getByName(selector);
            if (codeEnum == CodeEnum.NORMAL) {
                return;
            }
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
            } else {
                log.info("Selector: {}", selector);
                log.info("Logged in, starting initialization.");
                WeChatTransfer webWxNewLoginPage = this.cacheComponent.get(CacheEnum.BASE_REQUEST, WeChatTransfer.class);

                WeChatTransfer webWxInit = this.weChatClientComponent.postWebWxInit(webWxNewLoginPage);

                WeChatSyncKeyTransfer weChatSyncKeyTransfer = webWxInit.getSyncKey();
                webWxNewLoginPage.setSyncKey(weChatSyncKeyTransfer);
                WeChatTransfer webWxGetContact = this.weChatClientComponent.postWebWxGetContact(webWxNewLoginPage);

                Map<String, WeChatContactTransfer> weChatContactTransferMap = webWxGetContact.getMemberList()
                        .stream()
                        .collect(Collectors.toMap(WeChatContactTransfer::getUserName, Function.identity(), (first, second) -> first));
                WeChatContactTransfer user = webWxInit.getUser();
                weChatContactTransferMap.put(user.getUserName(), user);

                this.cacheComponent.put(CacheEnum.USER, user);
                this.cacheComponent.put(CacheEnum.WE_CHAT_CONTACT_TRANSFER_MAP, weChatContactTransferMap);

                this.weChatTransfer = webWxNewLoginPage;
                this.weChatContactTransferMap = weChatContactTransferMap;
                log.info("The initialization is completed and synchronization of messages begins.");
            }
        } else {
            log.error("Ret Code: {}", retCode);
            Thread.currentThread().interrupt();
            if (Thread.currentThread().isInterrupted()) {
                this.configurableApplicationContext.close();
            }
        }
    }

    public CronTask getTask() {
        return new CronTask(this, "* * * * * *");
    }
}
