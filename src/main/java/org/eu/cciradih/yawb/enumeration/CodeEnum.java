package org.eu.cciradih.yawb.enumeration;

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
    X_SUCCESS("0"), //成功
    HAS_MSG("2"),
    RE_CALL("3"),
    TICKET_ERROR("-14"), //ticket错误
    PARAM_ERROR("1"), //传入参数错误
    NOT_LOGIN_WARN("1100"), //未登录提示
    NOT_LOGIN_CHECK("1101"), //未检测到登录
    COOKIE_INVALID_ERROR("1102"), //cookie值无效
    LOGIN_ENV_ERROR("1203"), //当前登录环境异常，为了安全起见请不要在web端进行登录
    TOO_OFEN("1205"), //操作频繁
    ;

    private final String name;

    public static CodeEnum getByName(String name) {
        return Arrays.stream(values())
                .filter(codeEnum -> codeEnum.getName().equals(name))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
