package com.ustack.chat.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 定义队列
    @Bean
    public Queue chatQueue() {
        return new Queue("chat.queue", true); // durable队列
    }

    // 定义交换机（示例使用直连交换机）
    @Bean
    public DirectExchange chatExchange() {
        return new DirectExchange("chat.exchange");
    }

    // 绑定队列和交换机
    @Bean
    public Binding bindingChatQueue(Queue chatQueue, DirectExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with("chat.routingKey");
    }

}
