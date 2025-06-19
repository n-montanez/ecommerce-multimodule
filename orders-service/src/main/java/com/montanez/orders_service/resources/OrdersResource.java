package com.montanez.orders_service.resources;

import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.montanez.orders_service.model.order.dto.CreateOrderDTO;
import com.montanez.orders_service.services.OrdersService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "user", "admin" })
public class OrdersResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    OrdersService ordersService;

    @GET
    @RolesAllowed({ "admin" })
    public Response getAllOrders() {
        return Response.ok(ordersService.getAllOrders()).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") UUID id) {
        System.out.println("By id");
        return Response.ok(ordersService.getOrderById(jwt, id)).build();
    }
    
    @GET
    @Path("/me")
    public Response getOrdersOfUser() {
        System.out.println("By user");
        return Response.ok(ordersService.getOrdersByUser(jwt)).build();
    }

    @POST
    public Response createOrder(@Valid CreateOrderDTO createOrder) {
        return Response.ok(ordersService.createOrder(jwt, createOrder)).build();
    }
}
