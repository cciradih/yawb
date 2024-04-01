package org.eu.cciradih.wechat.data.transfer;

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
