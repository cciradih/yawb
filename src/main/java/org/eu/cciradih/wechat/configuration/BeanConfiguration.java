package org.eu.cciradih.wechat.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.OkHttpClient;
import org.eu.cciradih.wechat.component.CookieJarComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.TimeUnit;

@Configuration
public class BeanConfiguration {
    @Bean
    public OkHttpClient httpClient(CookieJarComponent cookieJarComponent) {
        return new OkHttpClient.Builder()
                .cookieJar(cookieJarComponent)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(availableProcessors);
        return threadPoolTaskScheduler;
    }

    @Bean
    public Cache<String, String> cache() {
        return Caffeine.newBuilder()
                .build();
    }
}
