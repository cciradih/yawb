package org.eu.cciradih.yawb.component.state;

import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.WechatContext;
import org.eu.cciradih.yawb.component.WechatState;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UnauthenticatedWechatState implements WechatState {
    private final WechatContext wechatContext;

    public UnauthenticatedWechatState(WechatContext wechatContext) {
        this.wechatContext = wechatContext;
    }

    @Override
    public void handle() {
        log.info("Attempting to log in...");
        wechatContext.getQRCode();
        boolean success = wechatContext.login();
        if (success) {
            wechatContext.setWechatState(new AuthenticatedWechatState(wechatContext));
        }
    }
}

