package org.eu.cciradih.yawb.interceptor;

import org.eu.cciradih.yawb.data.transfer.WeChatSendMsgTransfer;
import org.eu.cciradih.yawb.enumeration.MsgTypeEnum;

public interface BotInterceptor {
    WeChatSendMsgTransfer send(MsgTypeEnum msgTypeEnum, String fromUserName, String toUserName, String content);

    default WeChatSendMsgTransfer next() {
        return WeChatSendMsgTransfer.builder()
                .isNull(true)
                .build();
    }

    default WeChatSendMsgTransfer next(String toUserName, String content) {
        return WeChatSendMsgTransfer.builder()
                .isNull(false)
                .toUserName(toUserName)
                .content(content)
                .build();
    }
}
