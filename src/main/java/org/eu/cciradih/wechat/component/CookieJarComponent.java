package org.eu.cciradih.wechat.component;

import lombok.Data;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
public class CookieJarComponent implements CookieJar {
    private final Map<String, List<Cookie>> COOKIE_LIST_MAP = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        COOKIE_LIST_MAP.put(httpUrl.host(), list);
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        List<Cookie> cookieList = COOKIE_LIST_MAP.get(httpUrl.host());
        if (cookieList == null) {
            return new ArrayList<>();
        }
        return cookieList;
    }
}
