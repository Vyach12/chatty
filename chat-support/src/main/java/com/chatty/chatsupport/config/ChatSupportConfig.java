package com.chatty.chatsupport.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatSupportConfig {

    @Value("spring.rabbitmq.queue.chat")
    private String queue;

    @Value("spring.rabbitmq.exchange")
    private String exchange;

    @Value("spring.rabbitmq.routing-key.chat")
    private String routingKey;

    @Bean
    public Queue chatQueue() {
        return new Queue(queue);
    }
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding chatBinding() {
        return BindingBuilder.bind(chatQueue()).to(exchange()).with(routingKey);
    }

}
