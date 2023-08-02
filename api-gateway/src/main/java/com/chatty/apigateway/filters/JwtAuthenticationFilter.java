package com.chatty.apigateway.filters;

import lombok.NoArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

public class AuthenticationPrefilter extends AbstractGatewayFilterFactory<AuthenticationPrefilter.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return null;
    }

    @NoArgsConstructor
    public static class Config {

    }
}
