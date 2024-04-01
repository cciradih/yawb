package org.eu.cciradih.wechat.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CodeEnum {
    TIMEOUT("408"),
    SUCCESS("200"),
    ;
    private final String name;

    public static CodeEnum getByName(String name) {
        return Arrays.stream(values())
                .filter(codeEnum -> codeEnum.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
