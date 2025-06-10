package com.ustack.chat.controller;

import cn.hutool.core.util.IdUtil;
import com.ustack.chat.config.SseClient;
import com.ustack.global.common.rest.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: ChatController
 * @Date: 2025-02-17 23:16
 */
@RestController
@RequestMapping("/api/v1/chatDemo")
@Slf4j
@RequiredArgsConstructor
public class ChatDemoController {

    private final SseClient sseClient;


    /**
     * 获取会话id
     * @author linxin
     * @return RestResponse
     * @date 2025/2/18 13:12
     */
    @GetMapping("/conversationId")
    public RestResponse conversationId() {
        // 创建新会话 返回会话id
        return RestResponse.success(IdUtil.simpleUUID());
    }

    /**
     * 创建SSE 连接
     * @param: uid
     * @author linxin
     * @return SseEmitter
     * @date 2025/2/17 23:29
     */
    @CrossOrigin
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestParam("conversationId") String conversationId) {

        return sseClient.createSse(conversationId);
    }


    /**
     * 测试指定SSE会话推送消息
     * @param: uid
     * @author linxin
     * @return String
     * @date 2025/2/17 23:29
     */
    @CrossOrigin
    @GetMapping("/sendSseMsg")
    @ResponseBody
    public RestResponse sseChat(@RequestParam("conversationId") String conversationId) {
        for (int i = 0; i < 10; i++) {
            sseClient.sendMessage(conversationId, "no"+i, IdUtil.fastUUID());
        }
        return RestResponse.okWithMsg("成功");
    }

    /**
     * 关闭连接
     */
    @CrossOrigin
    @GetMapping("/closeSse")
    public void closeConnect(String conversationId ){

        sseClient.closeSse(conversationId);
    }

}
