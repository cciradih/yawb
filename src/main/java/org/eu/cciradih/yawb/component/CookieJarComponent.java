package org.eu.cciradih.yawb.component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class CookieJarComponent implements CookieJar {
    private final Map<String, List<Cookie>> COOKIE_LIST_MAP = Collections.synchronizedMap(new HashMap<>());
    private static final String CACHE_FILE_PATH = "cache.cookie";

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        COOKIE_LIST_MAP.put(httpUrl.host(), list);
//        log.info("CookieJarComponent saveFromResponse: {}, {}", httpUrl.url(), list);
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        List<Cookie> cookieList = COOKIE_LIST_MAP.get(httpUrl.host());
        if (cookieList == null) {
            return new ArrayList<>();
        }
//        log.info("CookieJarComponent loadForRequest: {}, {}",  httpUrl.url(), cookieList);
        return cookieList;
    }

    public static long dateToMillis(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        try {
            return format.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return Long.MAX_VALUE;
        }
    }
    @PostConstruct
    public void init() {
        // 在Bean初始化时从文件中读取Cookie
        try (BufferedReader reader = new BufferedReader(new FileReader(CACHE_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String[] attributes = parts[1].split("; ");
                    String[] nameValue = attributes[0].split("=");
                    String name = nameValue[0];
                    String value = nameValue[1];
                    String domain = parts[0];
                    String path = "/";
                    long expiresAt = Long.MAX_VALUE;
                    for (String attribute : attributes) {
                        if (attribute.startsWith("domain=")) {
                            domain = attribute.substring("domain=".length());
                        } else if (attribute.startsWith("path=")) {
                            path = attribute.substring("path=".length());
                        } else if (attribute.startsWith("expires=")) {
                            expiresAt = dateToMillis(attribute.substring("expires=".length()));
                        }
                    }
                    Cookie cookie = new Cookie.Builder()
                            .name(name)
                            .value(value)
                            .domain(domain)
                            .path(path)
                            .expiresAt(expiresAt)
                            .secure()
                            .build();

                    COOKIE_LIST_MAP.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(cookie);
                }

            }

            log.info("Cookies have been loaded from file.");
        } catch (IOException e) {
            log.error("No existing cookie file found. Starting with an empty cookie store.");
        }
    }
    @PreDestroy
    public void destroy() {
        // 在Bean销毁之前保存Cookie到文件
        COOKIE_LIST_MAP.forEach((host, cookieList) -> {
            try {
                //写入文件
                File file = new File(CACHE_FILE_PATH);
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    for (Cookie cookie : cookieList) {
                        writer.write(host + "=" + cookie);
                        writer.newLine();
                    }
                }
            } catch (Exception e) {
                log.error("CookieJarComponent cleanUp error: {}", e.getMessage());
            }

        });

    }
}
