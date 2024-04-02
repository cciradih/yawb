package org.eu.cciradih.yawb.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.eu.cciradih.yawb.component.CookieJarComponent;
import org.eu.cciradih.yawb.interceptor.BotInterceptor;
import org.eu.cciradih.yawb.interceptor.BotInterceptorSort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
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

    @Bean
    public Map<Integer, BotInterceptor> botInterceptorMap(List<BotInterceptor> botInterceptorList) {
        botInterceptorList = botInterceptorList.stream()
                .filter(botInterceptor -> botInterceptor.getClass().getAnnotation(BotInterceptorSort.class) != null)
                .toList();
        List<Integer> sortList = botInterceptorList.stream().map(botInterceptor -> botInterceptor.getClass().getAnnotation(BotInterceptorSort.class).value())
                .toList();
        List<Integer> distinctSortList = sortList.stream().distinct().toList();
        if (sortList.size() != distinctSortList.size()) {
            log.error("Found duplicate sorted BotInterceptor. Please reset BotInterceptorSort.value().");
            throw new RuntimeException("Found duplicate sorted BotInterceptor. Please reset BotInterceptorSort.value().");
        }
        return botInterceptorList.stream()
                .collect(Collectors.toMap(botInterceptor ->
                        botInterceptor.getClass().getAnnotation(BotInterceptorSort.class).value(), Function.identity()));
    }
}
