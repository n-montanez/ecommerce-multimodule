package com.montanez.orders_service.resources;

import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "user" })
public class OrdersResource {

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({ "admin" })
    public Response getAllOrders() {
        return null;
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") UUID id) {
        return null;
    }

    @GET
    @Path("/me")
    public Response getOrdersOfUser() {
        return null;
    }
}
