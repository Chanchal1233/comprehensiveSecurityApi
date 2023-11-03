package com.security.gateway.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CustomRequestRateLimiterGatewayFilterFactory
        extends AbstractGatewayFilterFactory<CustomRequestRateLimiterGatewayFilterFactory.Config> {

    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver defaultKeyResolver;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> defaultKeyResolver.resolve(exchange).flatMap(key -> {
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            if (route == null) {
                return chain.filter(exchange);
            }
            return redisRateLimiter.isAllowed(route.getId(), key).flatMap(response -> {
                if (response.isAllowed()) {
                    return chain.filter(exchange);
                } else {
                    return handleRateLimitExceeded(exchange);
                }
            });
        });
    }

    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String responseBody = "{\"message\":\"Too many requests. Please try again later.\"}";
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes())));
    }

    @Getter
    @Setter
    public static class Config {
        private String replenishRate;
        private String burstCapacity;

    }
}