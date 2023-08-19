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

    @Value("spring.rabbitmq.queue.user")
    private String queue;

    @Value("spring.rabbitmq.exchange")
    private String exchange;

    @Value("spring.rabbitmq.routing-key.user")
    private String routingKey;

    @Bean
    public Queue userQueue() {
        return new Queue(queue);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }
    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue()).to(exchange()).with(routingKey);
    }
}
