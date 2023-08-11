package com.chatty.apigateway.config;

import com.chatty.apigateway.filters.JwtAuthenticationFilter;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("authentication-service", r -> r.path("/api/v1/auth/**")
                        .uri("lb://authentication-service"))
                .route("user-management-service", r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(new JwtAuthenticationFilter(webClientBuilder())))
                        .uri("lb://user-management-service"))
                .route("chat-support-service", r->r.path("/api/v1/chats/**")
                        .filters(f -> f.filter(new JwtAuthenticationFilter(webClientBuilder())))
                        .uri("lb://chat-support-service"))
                .route("eureka-server", r -> r.path("/eureka/web")
                        .filters(f -> f.setPath("/"))
                        .uri("http://localhost:8761"))
                .route("eureka-server-static", r -> r.path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
