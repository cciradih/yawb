package org.eu.cciradih.yawb.interceptor.impl;

import org.eu.cciradih.yawb.data.transfer.WeChatSendMsgTransfer;
import org.eu.cciradih.yawb.enumeration.MsgTypeEnum;
import org.eu.cciradih.yawb.interceptor.BotInterceptor;
import org.eu.cciradih.yawb.interceptor.BotInterceptorSort;

@BotInterceptorSort(1)
public class EchoBotInterceptor implements BotInterceptor {
    @Override
    public WeChatSendMsgTransfer send(MsgTypeEnum msgTypeEnum, String fromUserName, String toUserName, String content) {
        if (msgTypeEnum == MsgTypeEnum.TEXT) {
            content += "[Echo]";
            return this.next(fromUserName, content);
        }
        return this.next();
    }
}
