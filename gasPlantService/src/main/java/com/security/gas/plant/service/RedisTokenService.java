package com.security.gas.plant.service;

import com.security.gas.plant.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisTokenService {

    private final StringRedisTemplate redisTemplate;

    public void saveTokenToRedis(String token, UserDetails userDetails, long expiration) {
        String key = "token::" + token;

        redisTemplate.opsForHash().put(key, "username", userDetails.getUsername());

        String roles = userDetails.getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        redisTemplate.opsForHash().put(key, "roles", roles);

        String permissions = userDetails.getAuthorities()
                .stream()
                .filter(grantedAuthority -> !grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        redisTemplate.opsForHash().put(key, "permissions", permissions);
        redisTemplate.expire(key, expiration, TimeUnit.MILLISECONDS);
    }

    public void saveUserToRedis(UserDetails userDetails) {
        String key = "user::" + userDetails.getUsername();

        redisTemplate.opsForHash().put(key, "email", userDetails.getUsername());
        redisTemplate.opsForHash().put(key, "password", userDetails.getPassword());
        redisTemplate.opsForHash().put(key, "firstname", ((User) userDetails).getFirstname());
        redisTemplate.opsForHash().put(key, "lastname", ((User) userDetails).getLastname());

        String roles = userDetails.getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        redisTemplate.opsForHash().put(key, "roles", roles);

        String permissions = userDetails.getAuthorities()
                .stream()
                .filter(grantedAuthority -> !grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        redisTemplate.opsForHash().put(key, "permissions", permissions);
    }

    public Map<Object, Object> getUserDataFromRedis(String email) {
        String key = "user::" + email;
        return redisTemplate.opsForHash().entries(key);
    }

    public Map<Object, Object> getTokenDataFromRedis(String token) {
        String key = "token::" + token;
        return redisTemplate.opsForHash().entries(key);
    }

    public boolean isTokenBlacklisted(String token) {
        String key = "token::" + token;
        boolean isBlacklisted = !redisTemplate.hasKey(key);
        log.info("Token is blacklisted: {}", isBlacklisted);
        return isBlacklisted;
    }

    public void invalidateToken(String token) {
        String key = "token::" + token;
        redisTemplate.delete(key);
        log.info("Token invalidated: {}", token);
    }

    public String getTokenForUser(String username) {
        Set<String> keys = redisTemplate.keys("token::*");
        for (String key : keys) {
            String storedUsername = (String) redisTemplate.opsForHash().get(key, "username");
            if (username.equals(storedUsername)) {
                log.info("Fetched token from Redis for username: {}", username);
                return key.replace("token::", "");
            }
        }
        log.warn("No token found in Redis for username: {}", username);
        return null;
    }
}