package org.eu.cciradih.wechat.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MsgTypeEnum {
    UNKNOWN(-1),
    JUMP(51),
    TEXT(1),
    IMAGE(3),
    BUSINESS_CARD(42),
    VIDEO(43),
    EMOJI(47),
    FILE(49),
    VOICE(53),
    GROUP_VOICE(10000),
    ;
    private final Integer name;

    public static MsgTypeEnum getByName(Integer name) {
        return Arrays.stream(values())
                .filter(msgTypeEnum -> msgTypeEnum.getName().equals(name))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
