package com.chatty.authentication.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationConfig {
    @Value("${rabbitmq.queue.username-change}")
    private String usernameChangeQueue;
    @Value("${rabbitmq.routing-key.username-change}")
    private String usernameChangeRoutingKey;
    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Bean
    public Queue authenticationChangeUsernameQueue() {
        return new Queue(usernameChangeQueue);
    }
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }
    @Bean
    public Binding authenticationChangeUsernameBinding() {
        return BindingBuilder.bind(authenticationChangeUsernameQueue()).to(exchange()).with(usernameChangeRoutingKey);
    }
}
