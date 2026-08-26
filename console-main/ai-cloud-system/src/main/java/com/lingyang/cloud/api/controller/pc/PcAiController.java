package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lingyang.cloud.api.model.vo.AiResultVO;
import com.lingyang.cloud.entity.SysAiDialogue;
import com.lingyang.cloud.entity.SysAiDialogueMsg;
import com.lingyang.cloud.mapper.SysAiDialogueMapper;
import com.lingyang.cloud.mapper.SysAiDialogueMsgMapper;
import com.lingyang.cloud.model.dto.AiMsgDTO;
import com.lingyang.cloud.model.query.home.SysAIMsgQuery;
import com.lingyang.cloud.service.SysAiDialogueMsgService;
import com.lingyang.cloud.service.SysAiDialogueService;
import com.lingyang.cloud.utils.DoubaoUtils;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.IpUtils;
import com.volcengine.ark.runtime.exception.ArkHttpException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Tag(name = "pc端-AI")
@RestController
@RequestMapping("/pc/ai")
public class PcAiController {


    @Autowired
    private SysAiDialogueMsgMapper sysAiDialogueMsgMapper;

    @Autowired
    private SysAiDialogueMapper sysAiDialogueMapper;

    @Autowired
    private SysAiDialogueService sysAiDialogueService;

    @Autowired
    private SysAiDialogueMsgService sysAiDialogueMsgService;


    static ArkService service = ArkService.builder()
            .baseUrl("https://ark.cn-beijing.volces.com/api/v3/")
            .timeout(Duration.ofSeconds(120))
            .connectTimeout(Duration.ofSeconds(20))
            .retryTimes(3)
            .apiKey(DoubaoUtils.HSQY.API_KEY).build();



//    @GetMapping(path = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter streamMessage(@RequestParam String message) {
//        SseEmitter emitter = new SseEmitter();
//        // 模拟逐字输出
//        new Thread(() -> {
//            try {
//                long start = System.currentTimeMillis();
//                StringBuilder result = new StringBuilder();
//                final List<ChatMessage> streamMessages = new ArrayList<>();
//                final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
//                final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(message).build();
//                streamMessages.add(streamSystemMessage);
//                streamMessages.add(streamUserMessage);
//                ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
//                        .model(DoubaoUtils.HSQY.MODEL)
//                        .messages(streamMessages)
//                        .build();
//                try {
//                    service.streamChatCompletion(streamChatCompletionRequest)
//                            .doOnError(Throwable::printStackTrace)
//                            .blockingForEach(
//                                    choice -> {
//                                        if (choice.getChoices().size() > 0) {
//                                            Object content = choice.getChoices().get(0).getMessage().getContent();
//                                            emitter.send(SseEmitter.event().data(content.toString()));
//                                            result.append(content);
//                                        }
//                                    }
//                            );
//                } catch (ArkHttpException e) {
//                    log.info("调用分析异常", e);
//                }
////                service.shutdownExecutor();
//                long end = System.currentTimeMillis();
//                log.info("生成耗时: {}ms", end - start );
//                emitter.complete(); // 完成发送
//            } catch (Exception e) {
//                emitter.completeWithError(e);
//            }
//        }).start();
//        return emitter;
//    }
    @Operation(summary = "对话分页", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysAiDialogue>> page(HttpServletRequest request) {
        return sysAiDialogueService.getPage(PageQuery.build(), request);
    }

    @Operation(summary = "消息分页", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page-msg")
    public Result<PageResult<SysAiDialogueMsg>> pageMsg(SysAIMsgQuery query) {
        return sysAiDialogueMsgService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "开启新对话")
    @GetMapping("/add")
    public Result<String> add(HttpServletRequest request) {
        SysAiDialogue dialogue = new SysAiDialogue();
        try {
            dialogue.setUserId(SecurityContext.getUserInfo().getUserId());
        } catch (Exception e) {
            String header = request.getHeader("User-Agent");
            String ipAddr = IpUtils.getIpAddr();
            dialogue.setUserLingshi(header + "@" + ipAddr);
        }


        dialogue.setName("新对话");
        sysAiDialogueMapper.insert(dialogue);
        return Result.success(dialogue.getId().toString());
    }

    @Operation(summary = "删除对话")
    @GetMapping("/delete")
    public Result<String> delete(Long dialogueId) {
        sysAiDialogueMapper.deleteById(dialogueId);
        return Result.success();
    }


    @Operation(summary = "发送消息")
    @PostMapping("/send-msg")
    public ResponseEntity<StreamingResponseBody> sendMsg(@RequestBody AiMsgDTO dto) {
        StringBuilder result = new StringBuilder();
        StreamingResponseBody stream = out -> {
            final List<ChatMessage> streamMessages = new ArrayList<>();
            final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是逸云数智AI，是由逸云数智开发的 AI 人工智能助手").build();
            final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(dto.getMessage()).build();
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
                                        result.append(content);
                                    }
                                }
                        );
                SysAiDialogueMsg msg = new SysAiDialogueMsg();
                msg.setDialogueId(dto.getDialogueId());
                msg.setUserValue(dto.getMessage());
                msg.setAiValue(result.toString());
                sysAiDialogueMsgMapper.insert(msg);
            } catch (ArkHttpException e) {
                log.info("调用分析异常", e);
            }
            out.flush();
        };

        Long count = sysAiDialogueMsgMapper.selectCount(
                new LambdaQueryWrapper<SysAiDialogueMsg>()
                        .eq(SysAiDialogueMsg::getDialogueId, dto.getDialogueId())
        );
        if (count == 0) {
            sysAiDialogueMapper.update(null,
                    new LambdaUpdateWrapper<SysAiDialogue>()
                            .eq(SysAiDialogue::getId, dto.getDialogueId())
                            .set(SysAiDialogue::getName, dto.getMessage().length() > 5 ? dto.getMessage().substring(0, 5) + "..." : dto.getMessage())
                    );
        }





        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(stream);
    }


    @Operation(summary = "发送消息定时调用版")
    @PostMapping("/send-msg-refresh")
    public Result<String> sendMsgRefresh(@RequestBody AiMsgDTO dto) {

        Long count = sysAiDialogueMsgMapper.selectCount(
                new LambdaQueryWrapper<SysAiDialogueMsg>()
                        .eq(SysAiDialogueMsg::getDialogueId, dto.getDialogueId())
        );
        if (count == 0) {
            sysAiDialogueMapper.update(null,
                    new LambdaUpdateWrapper<SysAiDialogue>()
                            .eq(SysAiDialogue::getId, dto.getDialogueId())
                            .set(SysAiDialogue::getName, dto.getMessage().length() > 5 ? dto.getMessage().substring(0, 5) + "..." : dto.getMessage())
            );
        }

        SysAiDialogueMsg msg = new SysAiDialogueMsg();
        msg.setDialogueId(dto.getDialogueId());
        msg.setUserValue(dto.getMessage());
        msg.setAiValue("...");
        sysAiDialogueMsgMapper.insert(msg);

        new Thread(
                () -> {
                    StringBuilder result = new StringBuilder();
                    final List<ChatMessage> streamMessages = new ArrayList<>();
                    final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是逸云数智AI，是由逸云数智开发的 AI 人工智能助手").build();
                    final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(dto.getMessage()).build();
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
                                                result.append(content);
                                                sysAiDialogueMsgMapper.update(null,
                                                        new LambdaUpdateWrapper<SysAiDialogueMsg>()
                                                                .eq(SysAiDialogueMsg::getId, msg.getId())
                                                                .set(SysAiDialogueMsg::getAiValue, result.toString())
                                                );
                                            }
                                        }
                                );


                        sysAiDialogueMsgMapper.update(null,
                                new LambdaUpdateWrapper<SysAiDialogueMsg>()
                                        .eq(SysAiDialogueMsg::getId, msg.getId())
                                        .set(SysAiDialogueMsg::getStatus, 2)
                        );

                    } catch (ArkHttpException e) {
                        log.info("调用分析异常", e);
                    }
                }
        ).start();

        return Result.success(msg.getId().toString());
    }

    @Operation(summary = "发送消息定时调用")
    @GetMapping("/get-send-msg")
    public Result<AiResultVO> getSendMsg(Long id) {
        SysAiDialogueMsg dialogueMsg = sysAiDialogueMsgMapper.selectById(id);
        AiResultVO vo = new AiResultVO();
        vo.setId(id);
        vo.setAiValue(StringUtils.isNotBlank(dialogueMsg.getAiValue()) ? dialogueMsg.getAiValue() : "思考中...");
        vo.setFinish(dialogueMsg.getStatus() != 1);
        vo.setUserValue(dialogueMsg.getUserValue());
        return Result.success(vo);
    }



    /**
     * 开放的
     */

    @GetMapping("/send")
    public ResponseEntity<StreamingResponseBody> stream(@RequestParam String message) {
        StreamingResponseBody stream = out -> {
                                    long start = System.currentTimeMillis();
                StringBuilder result = new StringBuilder();
                final List<ChatMessage> streamMessages = new ArrayList<>();
                final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
                final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(message).build();
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
                                            result.append(content);
                                        }
                                    }
                            );
                } catch (ArkHttpException e) {
                    log.info("调用分析异常", e);
                }


                    // 发送回复
                    out.flush();

        };
        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(stream);
    }

}
