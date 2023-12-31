package com.chatty.usermanagement.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserManagementConfig {
    @Value("${rabbitmq.queue.user-creation}")
    private String createUserQueue;
    @Value("${rabbitmq.routing-key.user-management.user-creation}")
    private String createUserRoutingKey;
    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Bean
    public Queue userManagementCreateUserQueue() {
        return new Queue(createUserQueue);
    }
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }
    @Bean
    public Binding userManagementCreateUserBinding() {
        return BindingBuilder.bind(userManagementCreateUserQueue()).to(exchange()).with(createUserRoutingKey);
    }
}
