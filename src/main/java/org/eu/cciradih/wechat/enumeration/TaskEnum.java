package org.eu.cciradih.wechat.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskEnum {
    WEB_WX_NEW_LOGIN_PAGE_TASK("WebWxNewLoginPageTask"),
    SYNC_CHECK_TASK("SyncCheckTask"),
    ;
    private final String name;
}
