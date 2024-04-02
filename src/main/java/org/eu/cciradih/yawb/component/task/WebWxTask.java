package org.eu.cciradih.yawb.component.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.WeChatClientComponent;
import org.eu.cciradih.yawb.component.SchedulerComponent;
import org.eu.cciradih.yawb.data.transfer.WeChatContactTransfer;
import org.eu.cciradih.yawb.data.transfer.WeChatSyncKeyTransfer;
import org.eu.cciradih.yawb.data.transfer.WeChatTransfer;
import org.eu.cciradih.yawb.enumeration.CacheEnum;
import org.eu.cciradih.yawb.enumeration.CodeEnum;
import org.eu.cciradih.yawb.enumeration.TaskEnum;
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
public class WebWxTask implements Runnable, ExitCodeGenerator {
    private final WeChatClientComponent weChatClientComponent;
    private final SchedulerComponent schedulerComponent;
    private final SyncCheckTask syncCheckTask;
    private final Cache<String, String> cache;
    private final ObjectMapper objectMapper;
    private String uuid;

    @SneakyThrows
    @Override
    public void run() {
        WeChatTransfer weChatTransfer = this.weChatClientComponent.getLoginUri(uuid);
        String code = weChatTransfer.getCode();
        CodeEnum codeEnum = CodeEnum.getByName(code);
        switch (codeEnum) {
            case S_AWAIT -> log.info("Scanned, waiting to continue logging in.");
            case S_SUCCESS -> {
                log.info("Logged in, starting initialization.");
                String redirectUri = weChatTransfer.getRedirectUri();
                WeChatTransfer webWxNewLoginPage = this.weChatClientComponent.getWebWxNewLoginPage(redirectUri);
                this.cache.put(CacheEnum.BASE_REQUEST.getName(), this.objectMapper.writeValueAsString(webWxNewLoginPage));

                WeChatTransfer webWxInit = this.weChatClientComponent.postWebWxInit(webWxNewLoginPage);

                WeChatSyncKeyTransfer weChatSyncKeyTransfer = webWxInit.getSyncKey();
                webWxNewLoginPage.setSyncKey(weChatSyncKeyTransfer);
                WeChatTransfer webWxGetContact = this.weChatClientComponent.postWebWxGetContact(webWxNewLoginPage);

                Map<String, WeChatContactTransfer> weChatContactTransferMap = webWxGetContact.getMemberList()
                        .stream()
                        .collect(Collectors.toMap(WeChatContactTransfer::getUserName, Function.identity()));
                WeChatContactTransfer user = webWxInit.getUser();
                weChatContactTransferMap.put(user.getUserName(), user);

                this.cache.put(CacheEnum.USER.getName(), this.objectMapper.writeValueAsString(user));
                this.cache.put(CacheEnum.WE_CHAT_CONTACT_TRANSFER_MAP.getName(), this.objectMapper.writeValueAsString(weChatContactTransferMap));

                this.syncCheckTask.setWeChatTransfer(webWxNewLoginPage);
                this.syncCheckTask.setWeChatContactTransferMap(weChatContactTransferMap);
                this.schedulerComponent.start(TaskEnum.SYNC_CHECK_TASK.getName(), this.syncCheckTask.getTask());

                log.info("The initialization is completed and synchronization of messages begins.");
                this.schedulerComponent.stop(TaskEnum.WEB_WX_TASK.getName());
            }
            case S_TIMEOUT -> {
                log.info("Timeout, regenerate the QR Code Uri.");
                WeChatTransfer jsLogin = this.weChatClientComponent.getJsLogin();
                this.uuid = jsLogin.getUuid();
                String qrCodeUri = this.weChatClientComponent.getQrCodeUri(jsLogin.getUuid());
                log.info("QR Code Uri: {}", qrCodeUri);
            }
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
