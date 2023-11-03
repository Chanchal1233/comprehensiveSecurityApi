package com.security.gas.plant.exception;

public class MainServiceBusinessException extends RuntimeException{

    public MainServiceBusinessException(String message, Exception ex) {
        super(message);
    }
}