package com.lingyang.cloud.utils;

import com.volcengine.ark.runtime.exception.ArkHttpException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DoubaoUtils {

    public static void main(String[] args) {
        System.out.println(DoubaoUtils.getApi("你好"));
    }


    /**
     * 火山引擎
     */
    public interface HSQY {
        /**
         * APIKEY
         */
        String API_KEY = "74d077d1-79c1-4255-a29f-9b68c7239f43";

        /**
         * 推理点
         */
        String MODEL = "ep-20250423160943-prrb5";
//        String MODEL = "ep-20241211232002-c9fcd";
    }


    static ArkService service = ArkService.builder()
            .baseUrl("https://ark.cn-beijing.volces.com/api/v3/")
            .timeout(Duration.ofSeconds(120))
            .connectTimeout(Duration.ofSeconds(20))
            .retryTimes(3)
            .apiKey(DoubaoUtils.HSQY.API_KEY).build();

    public static String getApi(String value, OutputStream out) {
        long start = System.currentTimeMillis();
        StringBuilder result = new StringBuilder();
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(value).build();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);
        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                .model(DoubaoUtils.HSQY.MODEL)
                .messages(streamMessages)
                .build();
        try {
            service.streamChatCompletion(streamChatCompletionRequest)
                    .doOnError(Throwable::printStackTrace)
                    .blockingForEach(
                            choice -> {
                                if (choice.getChoices().size() > 0) {
                                    Object content = choice.getChoices().get(0).getMessage().getContent();
                                    out.write(content.toString().getBytes());
                                    // 发送回复
                                    out.flush();
                                    result.append(content);
                                }
                            }
                    );
        } catch (ArkHttpException e) {
            log.info("调用分析异常", e);
        }
        service.shutdownExecutor();
        long end = System.currentTimeMillis();
        log.info("生成耗时: {}ms", end - start );
        return result.toString();
    }

    public static String getApi(String value) {
        long start = System.currentTimeMillis();
        StringBuilder result = new StringBuilder();
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(value).build();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);
        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                .model(DoubaoUtils.HSQY.MODEL)
                .messages(streamMessages)
                .build();
        try {
//            service.createChatCompletion(streamChatCompletionRequest).getChoices().forEach(choice -> {
//                result.append(choice.getMessage().getContent());
//            });

            service.streamChatCompletion(streamChatCompletionRequest)
                    .doOnError(Throwable::printStackTrace)
                    .blockingForEach(
                            choice -> {
                                if (choice.getChoices().size() > 0) {
                                    System.out.print(choice.getChoices().get(0).getMessage().getContent());
                                    result.append(choice.getChoices().get(0).getMessage().getContent());
                                }
                            }
                    );
        } catch (ArkHttpException e) {
            log.info("调用分析异常", e);
        }
        // service.shutdownExecutor();
        long end = System.currentTimeMillis();
        log.info("生成耗时: {}ms", end - start );
        return result.toString();
    }

}
