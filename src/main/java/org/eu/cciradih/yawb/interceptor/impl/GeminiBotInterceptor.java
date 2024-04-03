package org.eu.cciradih.yawb.interceptor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.eu.cciradih.yawb.component.CacheComponent;
import org.eu.cciradih.yawb.data.WeChatContactTransfer;
import org.eu.cciradih.yawb.data.WeChatSendMsgTransfer;
import org.eu.cciradih.yawb.enumeration.CacheEnum;
import org.eu.cciradih.yawb.enumeration.MsgTypeEnum;
import org.eu.cciradih.yawb.interceptor.BotInterceptor;
import org.eu.cciradih.yawb.interceptor.BotInterceptorSort;
import org.eu.cciradih.yawb.interceptor.configuration.BotInterceptorConfiguration;
import org.eu.cciradih.yawb.interceptor.data.GeminiTransfer;

import java.io.IOException;
import java.util.List;

@Slf4j
@BotInterceptorSort(1)
@RequiredArgsConstructor
public class GeminiBotInterceptor implements BotInterceptor {
    private final BotInterceptorConfiguration botInterceptorConfiguration;
    private final CacheComponent cacheComponent;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    @Override
    public WeChatSendMsgTransfer send(MsgTypeEnum msgTypeEnum, String fromUserName, String toUserName, String content) {
        if (msgTypeEnum != MsgTypeEnum.TEXT) {
            return this.next();
        }
        String command = this.botInterceptorConfiguration.getGemini().getCommand();
        if (!content.startsWith(command)) {
            return this.next();
        }
        WeChatContactTransfer user = this.cacheComponent.get(CacheEnum.USER, WeChatContactTransfer.class);
        if (!toUserName.equals(user.getUserName())) {
            return this.next();
        }
        content = content.substring(command.length()).trim();
        try {
            GeminiTransfer geminiTransfer = this.postGenerateContent(content);
            content = geminiTransfer.getCandidates()
                    .stream()
                    .map(GeminiTransfer.Candidate::getContent)
                    .flatMap(content1 -> content1.getParts().stream())
                    .map(GeminiTransfer.Part::getText)
                    .reduce("", String::join);
        } catch (IOException e) {
            e.printStackTrace();
            return this.next(fromUserName, e.getMessage());
        }
        return this.next(fromUserName, content);
    }

    public GeminiTransfer postGenerateContent(String content) throws IOException {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(this.botInterceptorConfiguration.getGemini().getHost())
                .addPathSegments("v1beta/models/gemini-pro:generateContent")
                .addQueryParameter("key", this.botInterceptorConfiguration.getGemini().getKey())
                .build();

        GeminiTransfer.Part part = new GeminiTransfer.Part();
        part.setText(content);
        GeminiTransfer.Content content1 = new GeminiTransfer.Content();
        content1.setParts(List.of(part));
        GeminiTransfer geminiTransfer = new GeminiTransfer();
        geminiTransfer.setContents(List.of(content1));
        content = this.objectMapper.writeValueAsString(geminiTransfer);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(content, mediaType);

        Request request = new Request.Builder()
                .post(requestBody)
                .url(httpUrl)
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        ResponseBody responseBody = this.okHttpClient.newCall(request).execute().body();

        if (responseBody == null) {
            throw new RuntimeException();
        }
        String response = responseBody.string();
        return this.objectMapper.readValue(response, GeminiTransfer.class);
    }
}
