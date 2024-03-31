package org.eu.cciradih.wechat.component.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.wechat.component.HttpClientComponent;
import org.eu.cciradih.wechat.component.SchedulerComponent;
import org.eu.cciradih.wechat.data.WeChatContactTransfer;
import org.eu.cciradih.wechat.data.WeChatSyncKeyTransfer;
import org.eu.cciradih.wechat.data.WeChatTransfer;
import org.eu.cciradih.wechat.enumeration.TaskEnum;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class WebWxNewLoginPageTask implements Runnable, ExitCodeGenerator {
    private final HttpClientComponent httpClientComponent;
    private final SchedulerComponent schedulerComponent;
    private final SyncCheckTask syncCheckTask;
    private String uuid;

    @Override
    public void run() {
        WeChatTransfer weChatTransfer = this.httpClientComponent.getLoginUri(uuid);
        if (weChatTransfer.getCode().equals("408")) {
            WeChatTransfer jsLogin = this.httpClientComponent.getJsLogin();
            this.uuid = jsLogin.getUuid();
            String qrCodeUri = this.httpClientComponent.getQrCodeUri(jsLogin.getUuid());
            log.info("QR Code Uri: {}", qrCodeUri);
        }
        if (weChatTransfer.getCode().equals("200")) {
            String redirectUri = weChatTransfer.getRedirectUri();
            WeChatTransfer webWxNewLoginPage = this.httpClientComponent.getWebWxNewLoginPage(redirectUri);
            WeChatTransfer webWxInit = this.httpClientComponent.postWebWxInit(webWxNewLoginPage);
            WeChatContactTransfer user = webWxInit.getUser();
            WeChatSyncKeyTransfer weChatSyncKeyTransfer = webWxInit.getSyncKey();
            webWxNewLoginPage.setSyncKey(weChatSyncKeyTransfer);
            WeChatTransfer webWxGetContact = this.httpClientComponent.postWebWxGetContact(webWxNewLoginPage);
            Map<String, WeChatContactTransfer> weChatContactTransferMap = webWxGetContact.getMemberList()
                    .stream()
                    .collect(Collectors.toMap(WeChatContactTransfer::getUserName, Function.identity()));
            weChatContactTransferMap.put(user.getUserName(), user);
            this.syncCheckTask.setWeChatTransfer(webWxNewLoginPage);
            this.syncCheckTask.setWeChatContactTransferMap(weChatContactTransferMap);
            this.schedulerComponent.start(TaskEnum.SYNC_CHECK_TASK.getName(), this.syncCheckTask.getTask());
            this.schedulerComponent.stop(TaskEnum.WEB_WX_NEW_LOGIN_PAGE_TASK.getName());
        }
    }

    public CronTask getTask() {
        return new CronTask(this, "* * * * * *");
    }

    @Override
    public int getExitCode() {
        return 0;
    }
}
