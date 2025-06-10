package com.ustack.file.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: SseClient
 * @Date: 2025-02-17 23:17
 */
@Component
@Slf4j
public class SseClient {

    private static final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();


    /**
     * 创建连接
     */
    public SseEmitter createSse(String conversationId) {
        //默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0l);
        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", conversationId);
            sseEmitterMap.remove(conversationId);
        });
        //超时回调
        sseEmitter.onTimeout(() -> {
            log.info("[{}]连接超时...................", conversationId);
        });
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.info("[{}]连接异常,{}", conversationId, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(conversationId)
                                .name("发生异常！")
                                .data("发生异常请重试！")
                                .reconnectTime(3000));
                        sseEmitterMap.put(conversationId, sseEmitter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sseEmitterMap.put(conversationId, sseEmitter);
        log.info("[{}]创建sse连接成功！", conversationId);
        return sseEmitter;
    }

    /**
     * 给指定会话发送消息
     */
    public boolean sendMessage(String conversationId, String messageId, String message) {
        if (Objects.isNull(message)) {
            log.info("参数异常，msg为null", conversationId);
            return false;
        }
        SseEmitter sseEmitter = sseEmitterMap.get(conversationId);
        if (sseEmitter == null) {
            log.info("消息推送失败conversationId:[{}],没有创建连接，请重试。", conversationId);
            return false;
        }
        try {
            sseEmitter.send(SseEmitter.event().id(messageId).reconnectTime(1 * 60 * 1000L).data(message));
            log.info("用户{},消息id:{},推送成功:{}", conversationId, messageId, message);
            return true;
        } catch (Exception e) {
            sseEmitterMap.remove(conversationId);
            log.info("会话{},消息id:{},推送异常:{}", conversationId, messageId, e.getMessage());
            sseEmitter.complete();
            return false;
        }
    }

    /**
     * 断开
     *
     * @param conversationId
     */
    public void closeSse(String conversationId) {
        if (sseEmitterMap.containsKey(conversationId)) {
            SseEmitter sseEmitter = sseEmitterMap.get(conversationId);
            sseEmitter.complete();
            sseEmitterMap.remove(conversationId);
        } else {
            log.info("会话 {} 连接已关闭", conversationId);
        }

    }


}
