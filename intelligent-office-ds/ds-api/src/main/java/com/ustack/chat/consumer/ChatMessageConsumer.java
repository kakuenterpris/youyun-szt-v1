package com.ustack.chat.consumer;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageConsumer {

    @RabbitListener(queues = "chat.queue")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
        // 这里添加业务处理逻辑
    }

}
