package org.eu.cciradih.yawb.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonIgnore
    private boolean isNull;
}
