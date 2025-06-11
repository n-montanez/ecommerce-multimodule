package com.montanez.authentication.exceptions.auth;

import jakarta.ws.rs.core.Response;

public class UserNotRegisteredException extends BaseAuthException {
    public UserNotRegisteredException(String message) {
        super(message, Response.Status.NOT_FOUND.getStatusCode());
    }
}
