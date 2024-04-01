package org.eu.cciradih.wechat.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskEnum {
    WEB_WX_TASK("WebWxTask"),
    SYNC_CHECK_TASK("SyncCheckTask"),
    ;
    private final String name;
}
