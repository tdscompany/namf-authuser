package com.moura.authorization.exceptions;

public class MissingTenantIdException extends RuntimeException {
    public MissingTenantIdException(String message) {
        super(message);
    }
}
