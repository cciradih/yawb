package org.eu.cciradih.yawb.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.SchedulerComponent;
import org.eu.cciradih.yawb.component.WeChatClientComponent;
import org.eu.cciradih.yawb.component.task.WebWxTask;
import org.eu.cciradih.yawb.data.transfer.WeChatTransfer;
import org.eu.cciradih.yawb.enumeration.TaskEnum;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledFuture;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotApplicationRunner implements ApplicationRunner {
    private final WeChatClientComponent weChatClientComponent;
    private final SchedulerComponent schedulerComponent;
    private final WebWxTask webWxTask;

    @Override
    public void run(ApplicationArguments args) {
        WeChatTransfer jsLogin = this.weChatClientComponent.getJsLogin();
        this.webWxTask.setUuid(jsLogin.getUuid());
        ScheduledFuture<?> scheduledFuture = this.schedulerComponent.getScheduledFuture(TaskEnum.WEB_WX_TASK.getName());
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        this.schedulerComponent.start(TaskEnum.WEB_WX_TASK.getName(), this.webWxTask.getTask());
        String qrCodeUri = this.weChatClientComponent.getQrCodeUri(jsLogin.getUuid());
        log.info("QR Code Uri: {}", qrCodeUri);
    }
}
