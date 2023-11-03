package com.security.gateway.security;

import com.security.gateway.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;

@Component
public class GatewayJwtValidationFilter implements WebFilter {

    private final JwtService jwtService;

    @Autowired
    public GatewayJwtValidationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Request path: " + path);

        if ("/GAS-PLANT/api/v1/auth/login".equals(path) | "/GAS-PLANT/api/v1/auth/register".equals(path) | "/GAS-PLANT/api/v1/auth/initialization/launch".equals(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No valid Authorization header found");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }
        String jwt = authHeader.substring(7);
        if (jwtService.isTokenStructureValid(jwt)) {
            String username = jwtService.getUsernameFromToken(jwt);
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } else {
            System.out.println("JWT token structure is invalid");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }
    }
}
