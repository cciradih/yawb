package org.eu.cciradih.wechat.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeChatMsgTransfer {
    @JsonProperty("MsgId")
    private String msgId;
    @JsonProperty("FromUserName")
    private String fromUserName;
    @JsonProperty("ToUserName")
    private String toUserName;
    @JsonProperty("MsgType")
    private Integer msgType;
    @JsonProperty("Content")
    private String content;
}
