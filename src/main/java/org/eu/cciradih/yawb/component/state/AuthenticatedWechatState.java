package org.eu.cciradih.yawb.component.state;


import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.WechatContext;
import org.eu.cciradih.yawb.component.WechatState;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticatedWechatState implements WechatState {
    private final WechatContext wechatContext;

    public AuthenticatedWechatState(WechatContext wechatContext) {
        this.wechatContext = wechatContext;
    }

    @Override
    public void handle() {
        log.info("Authenticated. Starting to poll for messages...");
        wechatContext.initPage();
        wechatContext.setWechatState(new PollingWechatState(wechatContext));
    }
}
