package com.security.gateway.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Component
public class SecurityCheckFilter implements WebFilter {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("([a-zA-Z0-9_\\-]+)([\\<\\>\\'\\\"\\=\\(\\)])+");
    private static final Pattern XSS_PATTERN = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String queryString = exchange.getRequest().getURI().getQuery();
        if (queryString != null && (SQL_INJECTION_PATTERN.matcher(queryString).find() || XSS_PATTERN.matcher(queryString).find())) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return Mono.empty();
        }
        return chain.filter(exchange);
    }
}