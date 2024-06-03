package org.eu.cciradih.yawb.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.enumeration.CacheEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheComponent {
    private final Cache<String, String> cache;
    private final ObjectMapper objectMapper;
    private static final String CACHE_FILE_PATH = "cache.page";

    @SneakyThrows
    public void put(CacheEnum cacheEnum, Object value) {
        this.cache.put(cacheEnum.getName(), this.objectMapper.writeValueAsString(value));
    }

    @SneakyThrows
    public <T> T get(CacheEnum cacheEnum, Class<T> tClass) {
        String value = this.cache.getIfPresent(cacheEnum.getName());
        return StringUtils.hasText(value) ? this.objectMapper.readValue(value, tClass) : null;
    }

    @PostConstruct
    public void init() {

        // 在Bean初始化时从文件中读取Cookie
        try (BufferedReader reader = new BufferedReader(new FileReader(CACHE_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    this.cache.put(parts[0], parts[1]);
                }
            }
            log.info("Cache have been loaded from file.");
        } catch (IOException e) {
            log.error("CacheComponent init error: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        //写入文件
        try {
            File file = new File(CACHE_FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                log.error("CacheComponent createNewFile");

            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : this.cache.asMap().entrySet()) {
                    writer.write(entry.getKey() + "=" + entry.getValue());
                    writer.newLine();
                    log.error("CacheComponent write key: {}, value:{}", entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            log.error("CacheComponent cleanUp error: {}", e.getMessage());
        }
    }
}
