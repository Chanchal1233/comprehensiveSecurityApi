package com.security.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class ApiGatewaySecurityConfiguration {

    private final GatewayJwtValidationFilter gatewayJwtValidationFilter;
    private final RequestValidationFilter requestValidationFilter;
    private final SecurityCheckFilter securityCheckFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange()
                .pathMatchers("/GAS-PLANT/api/v1/auth/**").permitAll()
                .pathMatchers("/GAS-PLANT/api/v1/**").authenticated()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(requestValidationFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAt(securityCheckFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(gatewayJwtValidationFilter, SecurityWebFiltersOrder.FIRST);
        return http.build();
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                return Mono.just(authHeader);
            } else {
                return Mono.just(exchange.getRequest().getRemoteAddress().toString());
            }
        };
    }

    @Bean
    public CustomRequestRateLimiterGatewayFilterFactory customRequestRateLimiterGatewayFilterFactory(RedisRateLimiter redisRateLimiter, KeyResolver defaultKeyResolver) {
        return new CustomRequestRateLimiterGatewayFilterFactory(redisRateLimiter, defaultKeyResolver);
    }
}