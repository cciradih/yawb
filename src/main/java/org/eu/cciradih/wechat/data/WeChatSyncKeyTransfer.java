package org.eu.cciradih.wechat.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeChatSyncKeyTransfer {
    @JsonProperty("Key")
    private Long key;
    @JsonProperty("Val")
    private Long val;

    @JsonProperty("Count")
    private Integer count;
    @JsonProperty("List")
    private List<WeChatSyncKeyTransfer> weChatSyncKeyTransferList;

    @Override
    public String toString() {
        return weChatSyncKeyTransferList.stream()
                .map(weChatSyncKeyTransfer -> String.format("%s_%s", weChatSyncKeyTransfer.getKey(), weChatSyncKeyTransfer.getVal()))
                .collect(Collectors.joining("|"));
    }
}
