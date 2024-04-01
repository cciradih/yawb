package org.eu.cciradih.wechat.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheEnum {
    USER("user"),
    WE_CHAT_CONTACT_TRANSFER_MAP("weChatContactTransferMap"),
    BASE_REQUEST("baseRequest"),
    ;
    private final String name;
}
