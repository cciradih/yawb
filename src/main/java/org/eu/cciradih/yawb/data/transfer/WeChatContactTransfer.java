package org.eu.cciradih.yawb.data.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeChatContactTransfer {
    @JsonProperty("UserName")
    private String userName;
    @JsonProperty("NickName")
    private String nickName;
    @JsonProperty("RemarkName")
    private String remarkName;
    @JsonProperty("VerifyFlag")
    private Integer verifyFlag;
}
