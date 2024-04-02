package org.eu.cciradih.yawb.interceptor.impl;

import lombok.extern.slf4j.Slf4j;
import org.eu.cciradih.yawb.data.transfer.WeChatSendMsgTransfer;
import org.eu.cciradih.yawb.enumeration.MsgTypeEnum;
import org.eu.cciradih.yawb.interceptor.BotInterceptor;
import org.eu.cciradih.yawb.interceptor.BotInterceptorSort;

@Slf4j
@BotInterceptorSort(0)
public class DefaultBotInterceptor implements BotInterceptor {
    @Override
    public WeChatSendMsgTransfer send(MsgTypeEnum msgTypeEnum, String fromUserName, String toUserName, String content) {
        log.info("msgType: {}, fromUserName: {}, toUserName: {}, content: {}", msgTypeEnum.getName(), fromUserName, toUserName, content);
        return this.next();
    }
}
