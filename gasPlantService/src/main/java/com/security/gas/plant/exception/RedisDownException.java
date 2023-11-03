package com.security.gas.plant.exception;

public class RedisDownException extends RuntimeException {
    public RedisDownException(String message) {
        super(message);
    }
}