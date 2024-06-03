package org.eu.cciradih.yawb.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.component.WechatContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotApplicationRunner implements ApplicationRunner {
    private final WechatContext wechatContext;

    @Override
    public void run(ApplicationArguments args) {
        wechatContext.setWechatState(wechatContext.getWechatState());
    }
}
