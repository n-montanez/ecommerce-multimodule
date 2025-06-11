package com.montanez.authentication.resources;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import com.montanez.authentication.models.auth.CreateCustomer;
import com.montanez.authentication.models.auth.LoginRequest;
import com.montanez.authentication.models.auth.LoginResponse;
import com.montanez.authentication.services.AuthService;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path("/auth")
public class AuthResource {

    @Inject
    private AuthService authService;

    @POST
    @Path("/login")
    public Response performLogin(@RequestBody LoginRequest loginRequest) {
        LoginResponse result = authService.login(loginRequest);
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("/register")
    public Response createAccount(@Valid CreateCustomer customer) {
        return Response
        .status(201)
        .entity(authService.register(customer))
        .build();
    }
}
