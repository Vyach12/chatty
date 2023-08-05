package com.chatty.apigateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
/*public class JwtAuthenticationFilter implements GatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private final WebClient.Builder webClientBuilder;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("URL is - " + exchange.getRequest().getURI().getPath());
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if(token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                return webClientBuilder.build().get()
                        .uri("http://authentication-service/validateToken/{token}", token)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .flatMap(valid -> {
                            if(valid) {
                                return chain.filter(exchange);
                            }
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }

    public static class Config {

    }
}*/

public class JwtAuthenticationFilter implements GatewayFilter{

    private final WebClient.Builder webClientBuilder;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("URL is - " + exchange.getRequest().getURI().getPath());
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return webClientBuilder.build().get()
                    .uri("http://authentication-service/api/v1/auth/validateToken")
                    .header("Authorization", exchange.getRequest().getHeaders().getFirst("Authorization"))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(valid -> {
                        if(valid) {
                            return chain.filter(exchange);
                        }
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
