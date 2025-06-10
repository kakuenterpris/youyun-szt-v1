package com.ustack.chat.controller;

import com.ustack.chat.annotation.RequiresPermission;
import com.ustack.chat.service.HistoryChatService;
import com.ustack.dto.HistoryChatDTO;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author PingY
 * @Classname HistoryChatController
 * @Description TODO
 * @Date 2025/2/19
 * @Created by PingY
 */
@RestController
@RequestMapping("/api/v1/historyChat")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "历史会话接口", description = "历史会话相关操作")
public class HistoryChatController {

    private final HistoryChatService historyChatService;

    /**
     * 获取历史会话列表
     *
     * @param historyChatDTO
     * @return
     */
    @PostMapping("/historyChatList")
    @Operation(summary = "获取历史会话列表")
    public RestResponse historyChatList(@RequestBody HistoryChatDTO historyChatDTO) {
        return historyChatService.historyChatList(historyChatDTO);
    }

    /**
     * 历史会话消息列表
     *
     * @param historyChatDTO
     * @return
     */
    @GetMapping("/historyChatListDetail")
    @Operation(summary = "历史会话消息详情")
    public RestResponse historyChatListDetail(HistoryChatDTO historyChatDTO) {
        return historyChatService.historyChatListDetail(historyChatDTO);
    }

    /**
     * 删除单条会话
     * @param historyChatDTO
     * @return
     */
    @PostMapping("/deleteConversations")
    @Operation(summary = "删除单条会话")
    public RestResponse deleteConversations(@RequestBody HistoryChatDTO historyChatDTO) {
        return historyChatService.deleteConversations(historyChatDTO);
    }

}
