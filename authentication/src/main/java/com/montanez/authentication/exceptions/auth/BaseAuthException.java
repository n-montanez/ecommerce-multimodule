package com.montanez.authentication.exceptions.auth;

public abstract class BaseAuthException extends RuntimeException {
    private int statusCode;

    public BaseAuthException(String message, int code) {
        super(message);
        this.statusCode = code;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
