package org.eu.cciradih.wechat.component.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.wechat.component.HttpClientComponent;
import org.eu.cciradih.wechat.data.transfer.WeChatContactTransfer;
import org.eu.cciradih.wechat.data.transfer.WeChatMsgTransfer;
import org.eu.cciradih.wechat.data.transfer.WeChatTransfer;
import org.eu.cciradih.wechat.enumeration.CodeEnum;
import org.eu.cciradih.wechat.enumeration.MsgTypeEnum;
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
    private final HttpClientComponent httpClientComponent;
    private final ConfigurableApplicationContext configurableApplicationContext;
    private WeChatTransfer weChatTransfer;
    private Map<String, WeChatContactTransfer> weChatContactTransferMap;

    @Override
    public void run() {
        WeChatTransfer syncCheck = this.httpClientComponent.getSyncCheck(weChatTransfer);
        String retCode = syncCheck.getRetCode();
        CodeEnum codeEnum = CodeEnum.getByName(retCode);
        if (codeEnum == CodeEnum.X_SUCCESS) {
            String selector = syncCheck.getSelector();
            codeEnum = CodeEnum.getByName(selector);
            if (codeEnum == CodeEnum.HAS_MSG) {
                WeChatTransfer webWxSync = this.httpClientComponent.postWebWxSync(this.weChatTransfer);
                List<WeChatMsgTransfer> webWxSyncAddMsgList = webWxSync.getAddMsgList();
                if (!CollectionUtils.isEmpty(webWxSyncAddMsgList)) {
                    webWxSyncAddMsgList.forEach(weChatMsgTransfer -> {
                        Integer msgType = weChatMsgTransfer.getMsgType();
                        MsgTypeEnum msgTypeEnum = MsgTypeEnum.getByName(msgType);
                        String content = weChatMsgTransfer.getContent();
                        String fromUserName = weChatMsgTransfer.getFromUserName();
                        WeChatContactTransfer fromUser = this.weChatContactTransferMap.get(fromUserName);
                        if (fromUser != null) {
                            fromUserName = fromUser.getRemarkName().equals("") ? fromUser.getNickName() : fromUser.getRemarkName();
                        }
                        String toUserName = weChatMsgTransfer.getToUserName();
                        WeChatContactTransfer toUser = this.weChatContactTransferMap.get(toUserName);
                        if (toUser != null) {
                            toUserName = toUser.getRemarkName().equals("") ? toUser.getNickName() : toUser.getRemarkName();
                        }
                        log.info("msgType: {}, fromUserName: {}, toUserName: {}, content: {}", msgTypeEnum, fromUserName, toUserName, content);
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
