package org.eu.cciradih.yawb.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eu.cciradih.yawb.component.HttpClientComponent;
import org.eu.cciradih.yawb.data.query.BotQuery;
import org.eu.cciradih.yawb.data.transfer.WeChatContactTransfer;
import org.eu.cciradih.yawb.data.transfer.WeChatTransfer;
import org.eu.cciradih.yawb.enumeration.CacheEnum;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/bot")
@RestController
@RequiredArgsConstructor
public class BotController {
    private final Cache<String, String> cache;
    private final HttpClientComponent httpClientComponent;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @GetMapping("/user")
    public WeChatContactTransfer getUser() {
        String user = this.cache.getIfPresent(CacheEnum.USER.getName());
        return this.objectMapper.readValue(user, WeChatContactTransfer.class);
    }

    @SneakyThrows
    @GetMapping("/memberList")
    public Map<String, WeChatContactTransfer> getMemberList() {
        String weChatContactTransferMap = this.cache.getIfPresent(CacheEnum.WE_CHAT_CONTACT_TRANSFER_MAP.getName());
        return this.objectMapper.readValue(weChatContactTransferMap, new TypeReference<>() {
        });
    }

    @SneakyThrows
    @PostMapping("/msg")
    public void postMsg(@RequestBody BotQuery botQuery) {
        WeChatTransfer baseRequest = this.objectMapper.readValue(this.cache.getIfPresent(CacheEnum.BASE_REQUEST.getName()), WeChatTransfer.class);
        WeChatContactTransfer user = this.objectMapper.readValue(this.cache.getIfPresent(CacheEnum.USER.getName()), WeChatContactTransfer.class);
        baseRequest.setUser(user);
        baseRequest.setToUserName(botQuery.getToUserName());
        baseRequest.setContent(botQuery.getContent());
        this.httpClientComponent.postWebWxSendMsg(baseRequest);
    }
}
