package com.montanez.authentication.resources;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.montanez.authentication.models.auth.CreateCustomer;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/account")
public class AccountInfoResource {
    
    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "user"})
    public Response getCustomerProfile(@PathParam("id") Long id) {
        return null;
    }

    @GET
    @Path("/me")
    @RolesAllowed({"admin", "user"})
    public Response getAuthedProfile() {
        return null;
    }

    @PUT
    @Path("/me")
    @RolesAllowed({"admin", "user"})
    public Response updateAuthedProfile(CreateCustomer customer) {
        return null;
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin"})
    public Response updateUserProfile(@PathParam("id") Long id, CreateCustomer customer) {
        return null;
    }
}
