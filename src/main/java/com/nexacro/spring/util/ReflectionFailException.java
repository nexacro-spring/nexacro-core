package com.nexacro.spring.util;

public class ReflectionFailException extends RuntimeException {
    
    public ReflectionFailException(String message) {
        super(message);
    }

    public ReflectionFailException(String message, Throwable cause) {
        super(message, cause);
    }
}