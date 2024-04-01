package org.eu.cciradih.wechat.component.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.wechat.component.HttpClientComponent;
import org.eu.cciradih.wechat.data.transfer.WeChatContactTransfer;
import org.eu.cciradih.wechat.data.transfer.WeChatMsgTransfer;
import org.eu.cciradih.wechat.data.transfer.WeChatTransfer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        if (!"0".equals(retCode)) {
            log.error("Ret Code: {}", retCode);
            Thread.currentThread().interrupt();
            if (Thread.currentThread().isInterrupted()) {
                this.configurableApplicationContext.close();
            }
        }
        String selector = syncCheck.getSelector();
        if (StringUtils.hasText(selector)) {
            if ("2".equals(selector)) {
                WeChatTransfer webWxSync = this.httpClientComponent.postWebWxSync(this.weChatTransfer);
                List<WeChatMsgTransfer> webWxSyncAddMsgList = webWxSync.getAddMsgList();
                if (webWxSyncAddMsgList != null && !webWxSyncAddMsgList.isEmpty()) {
                    webWxSyncAddMsgList
                            .stream()
                            .filter(weChatMsgTransfer -> weChatMsgTransfer.getMsgType() == 1)
                            .forEach(weChatMsgTransfer -> {
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
                                log.info("fromUserName: {}, toUserName: {}, content: {}", fromUserName, toUserName, content);
                            });
                }
            }
        }
    }

    public CronTask getTask() {
        return new CronTask(this, "* * * * * *");
    }
}
