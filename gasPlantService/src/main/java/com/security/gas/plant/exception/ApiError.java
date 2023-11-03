package com.security.gas.plant.exception;

import java.time.LocalDateTime;

public record ApiError (
        String path,
        String message,
        int statusCode,
        LocalDateTime localDateTime
){
}