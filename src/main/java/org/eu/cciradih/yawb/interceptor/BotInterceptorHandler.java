package org.eu.cciradih.yawb.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eu.cciradih.yawb.component.WeChatClientComponent;
import org.eu.cciradih.yawb.data.WeChatContactTransfer;
import org.eu.cciradih.yawb.data.WeChatSendMsgTransfer;
import org.eu.cciradih.yawb.data.WeChatTransfer;
import org.eu.cciradih.yawb.enumeration.CacheEnum;
import org.eu.cciradih.yawb.enumeration.MsgTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BotInterceptorHandler {
    private final Map<Integer, BotInterceptor> botInterceptorMap;
    private final WeChatClientComponent weChatClientComponent;
    private final Cache<String, String> cache;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void send(MsgTypeEnum msgTypeEnum, String fromUser, String toUser, String content) {
        if (CollectionUtils.isEmpty(this.botInterceptorMap)) {
            return;
        }

        WeChatSendMsgTransfer chain = new WeChatSendMsgTransfer();
        chain.setToUserName(toUser);
        chain.setContent(content);

        this.botInterceptorMap.values()
                .forEach(botInterceptor -> {
                    WeChatSendMsgTransfer weChatSendMsgTransfer =
                            botInterceptor.send(msgTypeEnum, fromUser, chain.getToUserName(), chain.getContent());
                    if (weChatSendMsgTransfer.isNull()) {
                        return;
                    }
                    chain.setNull(false);
                    chain.setToUserName(weChatSendMsgTransfer.getToUserName());
                    chain.setContent(weChatSendMsgTransfer.getContent());
                });

        if (chain.isNull()) {
            return;
        }

        WeChatTransfer baseRequest = this.objectMapper.readValue(this.cache.getIfPresent(CacheEnum.BASE_REQUEST.getName()), WeChatTransfer.class);
        WeChatContactTransfer user = this.objectMapper.readValue(this.cache.getIfPresent(CacheEnum.USER.getName()), WeChatContactTransfer.class);
        baseRequest.setUser(user);
        baseRequest.setToUserName(chain.getToUserName());
        baseRequest.setContent(chain.getContent());
        this.weChatClientComponent.postWebWxSendMsg(baseRequest);
    }
}
