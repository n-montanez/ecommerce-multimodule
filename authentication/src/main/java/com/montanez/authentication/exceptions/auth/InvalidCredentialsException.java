package com.montanez.authentication.exceptions.auth;

import jakarta.ws.rs.core.Response;

public class InvalidCredentialsException extends BaseAuthException {
    public InvalidCredentialsException(String message) {
        super(message, Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
