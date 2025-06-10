package com.montanez.authentication.resources;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import com.montanez.authentication.models.auth.CreateCustomer;
import com.montanez.authentication.models.auth.LoginRequest;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
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
    
    @POST
    @Path("/login")
    public Response performLogin(@RequestBody LoginRequest loginRequest) {
        return Response.ok().build();
    }

    @POST
    @Path("/register")
    public Response createAccount(@Valid CreateCustomer customer) {
        return Response.ok().build();
    }
}
