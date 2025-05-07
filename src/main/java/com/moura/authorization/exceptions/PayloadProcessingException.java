package com.moura.authorization.exceptions;

public class PayloadProcessingException extends RuntimeException {
    public PayloadProcessingException(String message) {
        super(message);
    }
}
