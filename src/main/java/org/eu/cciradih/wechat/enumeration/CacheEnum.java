package org.eu.cciradih.wechat.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheEnum {
    UUID("uuid"),
    WEB_WX_NEW_LOGIN_PAGE("webWxNewLoginPage"),
    WEB_WX_INIT("webWxInit"),
    WEB_WX_GET_CONTACT("webWxGetContact"),
    ;
    private final String name;
}
