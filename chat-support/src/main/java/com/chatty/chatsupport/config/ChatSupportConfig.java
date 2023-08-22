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
    @Value("${rabbitmq.queue.user-creation}")
    private String createUserQueue;
    @Value("${rabbitmq.queue.username-change}")
    private String usernameChangeQueue;
    @Value("${rabbitmq.routing-key.chat-support.user-creation}")
    private String createUserRoutingKey;
    @Value("${rabbitmq.routing-key.username-change}")
    private String changeUsernameRoutingKey;
    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Bean
    public Queue chatSupportCreateUserQueue() {
        return new Queue(createUserQueue);
    }

    @Bean
    public Queue chatSupportChangeUsernameQueue() {
        return new Queue(usernameChangeQueue);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }
    @Bean
    public Binding chatSupportCreateUserBinding() {
        return BindingBuilder.bind(chatSupportCreateUserQueue()).to(exchange()).with(createUserRoutingKey);
    }

    @Bean
    public Binding chatSupportChangeUsernameBinding(){
        return BindingBuilder.bind(chatSupportChangeUsernameQueue()).to(exchange()).with(changeUsernameRoutingKey);
    }
}
