package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.entity.SysVolumeEntity;
import com.lingyang.cloud.model.query.volume.SysVolumeQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.volcengine.ark.runtime.exception.ArkHttpException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.model.embeddings.EmbeddingRequest;
import com.volcengine.ark.runtime.model.embeddings.EmbeddingResult;
import com.volcengine.ark.runtime.service.ArkService;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.bridge.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pc/sixi")
public class TestSIXIController {

    public static final String API_KEY = "0ddda82c-076a-42f4-aa7c-f49dfd8e9983";
    public static final String MODEL = "ep-20241127175913-7s8s6";


    @GetMapping("/caiyun")
    public Result<String> caiyun(String value) {
        value += "从易经的角度分析一下今年我的财运， 400字以内，带分类加粗的简短分析一下。直接输出我的结果，不需要总结。";
         String result = getApi(value);
        return Result.success(result);
    }

    @GetMapping("/api")
    public Result<String> api(String value) {
        return Result.success(getApi(value));
    }

    public static void main(String[] args) {
        System.out.println(getApi("我是90后的龍性别女从事公务员从易经的角度分析一下今年我的财运， 400字以内，带分类加粗的简短分析一下。直接输出我的结果，不需要总结。"));
    }

    public static String getApi (String value) {
        StringBuilder result = new StringBuilder("");
        String apiKey = API_KEY;
        ArkService service = ArkService.builder()
                .baseUrl("https://ark.cn-beijing.volces.com/api/v3/")
                .timeout(Duration.ofSeconds(120))
                .connectTimeout(Duration.ofSeconds(20))
                .retryTimes(3)
                .apiKey(apiKey).build();
        System.out.println("\n----- streaming request -----");
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(value).build();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);
        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(streamMessages)
                .build();
        try {
            service.createChatCompletion(streamChatCompletionRequest).getChoices().forEach(choice -> {
                result.append(choice.getMessage().getContent());
            });

//            service.streamChatCompletion(streamChatCompletionRequest)
//                    .doOnError(Throwable::printStackTrace)
//                    .blockingForEach(
//                            choice -> {
//                                if (choice.getChoices().size() > 0) {
//                                    System.out.print(choice.getChoices().get(0).getMessage().getContent());
//                                }
//                            }
//                    );
        } catch (ArkHttpException e) {
            System.out.print(e.toString());
        }
        service.shutdownExecutor();
        return result.toString();
    }

}
