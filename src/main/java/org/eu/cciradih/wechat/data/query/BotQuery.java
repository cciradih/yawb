package org.eu.cciradih.wechat.data.query;

import lombok.Data;

@Data
public class BotQuery {
    private String toUserName;
    private String content;
}
