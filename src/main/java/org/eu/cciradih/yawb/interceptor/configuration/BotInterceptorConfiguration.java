package org.eu.cciradih.yawb.interceptor.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("bot.interceptor")
public class BotInterceptorConfiguration {
    private Gemini gemini;

    @Data
    public static class Gemini {
        private String command;
        private String host;
        private String key;
    }
}
