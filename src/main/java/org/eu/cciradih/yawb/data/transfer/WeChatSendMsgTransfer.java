package org.eu.cciradih.yawb.data.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeChatSendMsgTransfer {
    @JsonProperty("LocalId")
    private String localId;
    @JsonProperty("ClientMsgId")
    private String clientMsgId;
    @JsonProperty("MediaId")
    private String mediaId;
    @JsonProperty("FromUserName")
    private String fromUserName;
    @JsonProperty("ToUserName")
    private String toUserName;
    @JsonProperty("Type")
    private Integer type;
    @JsonProperty("Content")
    private String content;
}
