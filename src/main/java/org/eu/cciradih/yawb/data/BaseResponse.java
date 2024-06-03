package org.eu.cciradih.yawb.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseResponse {
    @JsonProperty("Ret")
    private String ret;
    @JsonProperty("ErrMsg")
    private String errMsg;
}
