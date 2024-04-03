package org.eu.cciradih.yawb.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eu.cciradih.yawb.enumeration.CacheEnum;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheComponent {
    private final Cache<String, String> cache;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void put(CacheEnum cacheEnum, Object value) {
        this.cache.put(cacheEnum.getName(), this.objectMapper.writeValueAsString(value));
    }

    @SneakyThrows
    public <T> T get(CacheEnum cacheEnum, Class<T> tClass) {
        String value = this.cache.getIfPresent(cacheEnum.getName());
        return this.objectMapper.readValue(value, tClass);
    }
}
