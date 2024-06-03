package org.eu.cciradih.yawb.component.state;

import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.WechatContext;
import org.eu.cciradih.yawb.component.WechatState;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PollingWechatState implements WechatState {
    private final WechatContext wechatContext;

    public PollingWechatState(WechatContext wechatContext) {
        this.wechatContext = wechatContext;
    }

    @Override
    public void handle() {
        while (true) {
            int errorCode = wechatContext.pollForMessages();
            if (errorCode == -1) {
                log.info("Session expired. Re-authenticating...");
                wechatContext.setWechatState(new UnauthenticatedWechatState(wechatContext));
                break;
            }
            try {
                Thread.sleep(50); // Poll every 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
