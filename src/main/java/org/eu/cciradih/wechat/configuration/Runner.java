package org.eu.cciradih.wechat.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.wechat.component.HttpClientComponent;
import org.eu.cciradih.wechat.component.SchedulerComponent;
import org.eu.cciradih.wechat.component.task.WebWxNewLoginPageTask;
import org.eu.cciradih.wechat.data.WeChatTransfer;
import org.eu.cciradih.wechat.enumeration.TaskEnum;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledFuture;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Runner implements ApplicationRunner {
    private final HttpClientComponent httpClientComponent;
    private final SchedulerComponent schedulerComponent;
    private final WebWxNewLoginPageTask webWxNewLoginPageTask;

    @Override
    public void run(ApplicationArguments args) {
        WeChatTransfer jsLogin = this.httpClientComponent.getJsLogin();
        this.webWxNewLoginPageTask.setUuid(jsLogin.getUuid());
        ScheduledFuture<?> scheduledFuture = this.schedulerComponent.getScheduledFuture(TaskEnum.WEB_WX_NEW_LOGIN_PAGE_TASK.getName());
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        this.schedulerComponent.start(TaskEnum.WEB_WX_NEW_LOGIN_PAGE_TASK.getName(), this.webWxNewLoginPageTask.getTask());
        String qrCodeUri = this.httpClientComponent.getQrCodeUri(jsLogin.getUuid());
        log.info("QR Code Uri: {}", qrCodeUri);
    }
}
