package org.eu.cciradih.yawb.data.transfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeChatTransfer {
    private String code;
    private String uuid;
    private String userAvatar;
    private String redirectUri;

    private String host;

    @JsonProperty("DeviceID")
    private String deviceId;
    @JsonProperty("Sid")
    private String wxSid;
    @JsonProperty("Skey")
    private String sKey;
    @JsonProperty("Uin")
    private String wxUin;
    @JsonIgnore
    private String passTicket;

    @JsonProperty("BaseRequest")
    private WeChatTransfer baseRequest;

    @JsonProperty("ContactList")
    private List<WeChatContactTransfer> contactList;

    @JsonProperty("SyncKey")
    private WeChatSyncKeyTransfer syncKey;

    @JsonProperty("User")
    private WeChatContactTransfer user;

    @JsonProperty("MemberList")
    private List<WeChatContactTransfer> memberList;

    @JsonProperty("retcode")
    private String retCode;

    private String selector;

    @JsonProperty("SyncCheckKey")
    private WeChatSyncKeyTransfer checkSyncKey;

    @JsonProperty("AddMsgList")
    private List<WeChatMsgTransfer> addMsgList;

    @JsonProperty("Msg")
    private WeChatSendMsgTransfer msg;

    @JsonProperty("Scene")
    private Integer scene;

    private String toUserName;

    private String content;
}
