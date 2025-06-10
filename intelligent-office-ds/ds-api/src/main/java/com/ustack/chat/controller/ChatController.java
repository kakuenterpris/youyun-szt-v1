package com.ustack.chat.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.ustack.chat.annotation.CommonPermission;
import com.ustack.chat.annotation.RequiresPermission;
import com.ustack.chat.properties.AiConfigProperties;
import com.ustack.chat.service.ChatService;
import com.ustack.dto.*;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;

/**
 * 对话接口
 *
 * @author zhangwei
 * @date 2025年02月18日
 */
@RestController
@RequestMapping("/api/v1/chat")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "智能对话", description = "智能对话相关操作")
public class ChatController {

    private final ChatService questionAnswerService;

    @Autowired
    private AiConfigProperties aiConfigProperties;
    private static final String[] whiteFileList = {"TXT", "MARKDOWN", "MDX", "PDF", "HTML", "XLSX", "XLS", "DOCX", "CSV", "MD", "HTM",
            "txt", "markdown", "mdx", "pdf", "html", "xlsx", "xls", "docx", "csv", "md", "htm"};


    /**
     * 创建会话
     * @return
     */
//    @GetMapping("/createConversation")
//    public RestResponse createConversation(@RequestParam("type") String type) {
//        return questionAnswerService.createConversation(type);
//    }

    /**
     * 智能问答接口
     *
     * @param chatRequestDto
     * @return
     */
    @PostMapping(value = "/common", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "智能问答接口")
    public SseEmitter common(@RequestBody ChatRequestDto chatRequestDto) {
        if (null != chatRequestDto.getFiles() && chatRequestDto.getFiles().size() > 5) {
            throw new RuntimeException("可上传文件个数不能超过5个");
        }
        return questionAnswerService.common(chatRequestDto);
    }

    /**
     * 推荐问答列表
     *
     * @param recommendChatDto
     * @return
     */
    @PostMapping("/recommendList")
//    @RequiresPermission("chat:recommendList")
    @Operation(summary = "推荐问答接口")
    public RestResponse recommendList(@RequestBody RecommendChatDto recommendChatDto) {
        return questionAnswerService.recommendList(recommendChatDto);
    }
    @PostMapping("/rename")
    @Operation(summary = "重命名会话接口")
    public RestResponse rename(@RequestBody RenameChatDto recommendChatDto) {
        return questionAnswerService.renameConversation(recommendChatDto);
    }

    @PostMapping("/stop")
    @Operation(summary = "停止会话接口")
    public RestResponse stopConversation(@RequestBody StopChatDto stopChatDto) {
        return questionAnswerService.stopConversation(stopChatDto);
    }

    /**
     * 上传智能问答文件
     *
     * @param file
     * @param sceneType
     * @return
     */
    @PostMapping(value = "/upload")
    @Operation(summary = "上传文件接口")
    public RestResponse upload(@RequestParam(value = "file") MultipartFile file,
                               @RequestParam(value = "sceneType") String sceneType) {
        String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());
        if (Arrays.stream(whiteFileList).noneMatch(s -> suffix.equals(s))) {
            return RestResponse.error(String.format("%s 文件不支持上传，如有特殊上传需求请联系管理员！", suffix));
        }
        if (file.getSize() > 1024 * 15 * 1024) {
            return RestResponse.error("文件上传最大不能超过15M，如有特殊上传需求请联系管理员！");
        }
        return questionAnswerService.uploadFileToAi(file, sceneType);
    }

}
