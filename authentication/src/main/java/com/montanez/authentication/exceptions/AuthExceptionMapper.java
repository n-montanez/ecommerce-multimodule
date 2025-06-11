package com.montanez.authentication.exceptions;

import com.montanez.authentication.exceptions.auth.BaseAuthException;
import com.montanez.authentication.models.api.ErrorResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AuthExceptionMapper implements ExceptionMapper<BaseAuthException> {

    @Override
    public Response toResponse(BaseAuthException exception) {
        System.out.println("Custom handling exception...");
        return Response.status(Response.Status.fromStatusCode(exception.getStatusCode()))
                .entity(new ErrorResponse(exception.getMessage(), exception.getStatusCode()))
                .build();
    }

}
