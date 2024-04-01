package org.eu.cciradih.wechat.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CodeEnum {
    UNKNOWN("-1"),
    S_AWAIT("201"),
    S_SUCCESS("200"),
    S_TIMEOUT("408"),
    X_SUCCESS("0"),
    HAS_MSG("2"),
    ;
    private final String name;

    public static CodeEnum getByName(String name) {
        return Arrays.stream(values())
                .filter(codeEnum -> codeEnum.getName().equals(name))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
