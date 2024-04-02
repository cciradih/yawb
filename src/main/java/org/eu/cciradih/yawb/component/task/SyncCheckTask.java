package org.eu.cciradih.yawb.component.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.WeChatClientComponent;
import org.eu.cciradih.yawb.data.WeChatContactTransfer;
import org.eu.cciradih.yawb.data.WeChatMsgTransfer;
import org.eu.cciradih.yawb.data.WeChatTransfer;
import org.eu.cciradih.yawb.enumeration.CodeEnum;
import org.eu.cciradih.yawb.enumeration.MsgTypeEnum;
import org.eu.cciradih.yawb.interceptor.BotInterceptorHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

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

    @Override
    public void run() {
        WeChatTransfer syncCheck = this.weChatClientComponent.getSyncCheck(weChatTransfer);
        String retCode = syncCheck.getRetCode();
        CodeEnum codeEnum = CodeEnum.getByName(retCode);
        if (codeEnum == CodeEnum.X_SUCCESS) {
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
