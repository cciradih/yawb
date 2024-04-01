package org.eu.cciradih.wechat.controller;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.eu.cciradih.wechat.component.HttpClientComponent;
import org.eu.cciradih.wechat.data.query.BotQuery;
import org.eu.cciradih.wechat.data.transfer.WeChatContactTransfer;
import org.eu.cciradih.wechat.data.transfer.WeChatTransfer;
import org.eu.cciradih.wechat.enumeration.CacheEnum;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/bot")
@RestController
@RequiredArgsConstructor
public class BotController {
    private final Cache<String, Object> cache;
    private final HttpClientComponent httpClientComponent;

    @GetMapping("/user")
    public WeChatContactTransfer getUser() {
        WeChatTransfer webWxInit = (WeChatTransfer) this.cache.getIfPresent(CacheEnum.WEB_WX_INIT.getName());
        if (webWxInit == null) {
            throw new RuntimeException();
        }
        return webWxInit.getUser();
    }

    @GetMapping("/memberList")
    public List<WeChatContactTransfer> getMemberList() {
        WeChatTransfer webWxGetContact = (WeChatTransfer) this.cache.getIfPresent(CacheEnum.WEB_WX_GET_CONTACT.getName());
        if (webWxGetContact == null) {
            throw new RuntimeException();
        }
        return webWxGetContact.getMemberList();
    }

    @PostMapping("/msg")
    public void sendMsg(@RequestBody BotQuery botQuery) {
        WeChatTransfer webWxNewLoginPage = (WeChatTransfer) this.cache.getIfPresent(CacheEnum.WEB_WX_NEW_LOGIN_PAGE.getName());
        WeChatTransfer webWxInit = (WeChatTransfer) this.cache.getIfPresent(CacheEnum.WEB_WX_INIT.getName());
        if (webWxNewLoginPage == null || webWxInit == null) {
            throw new RuntimeException();
        }
        webWxNewLoginPage.setUser(webWxInit.getUser());
        webWxNewLoginPage.setToUserName(botQuery.getToUserName());
        webWxNewLoginPage.setContent(botQuery.getContent());
        this.httpClientComponent.postWebWxSendMsg(webWxNewLoginPage);
    }
}
