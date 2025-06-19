package com.montanez.orders_service.resources;

import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.montanez.orders_service.model.order.dto.CreateOrderDTO;
import com.montanez.orders_service.model.order.dto.OrderInfoDTO;
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
@Tag(name = "Order Management", description = "Operations related to customer orders.")
public class OrdersResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    OrdersService ordersService;

    @GET
    @RolesAllowed({ "admin" })
    @Operation(summary = "Get all orders",
               description = "Retrieves a list of all orders in the system. Requires 'admin' role.")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved all orders",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = OrderInfoDTO.class, type = SchemaType.ARRAY)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        ),
        @APIResponse(
            responseCode = "403",
            description = "Forbidden - User does not have 'admin' role",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        )
    })
    public Response getAllOrders() {
        return Response.ok(ordersService.getAllOrders()).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get order by ID",
               description = "Retrieves a specific order by its UUID. Requires 'user' role" +
                             "or 'user' role if the order belongs to the authenticated user.")
    @Parameter(name = "id", description = "UUID of the order to retrieve", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved order",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = OrderInfoDTO.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        ),
        @APIResponse(
            responseCode = "403",
            description = "Forbidden - User does not have access to this order (not admin and not owner)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        ),
        @APIResponse(
            responseCode = "404",
            description = "Not Found - Order not found for the given ID",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        )
    })
    public Response getOrderById(@PathParam("id") UUID id) {
        System.out.println("By id");
        return Response.ok(ordersService.getOrderById(jwt, id)).build();
    }
    
    @GET
    @Path("/me")
    @Operation(summary = "Get orders of authenticated user",
               description = "Retrieves all orders placed by the currently authenticated user.")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved orders for the authenticated user",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = OrderInfoDTO.class, type = SchemaType.ARRAY)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        )
    })
    public Response getOrdersOfUser() {
        System.out.println("By user");
        return Response.ok(ordersService.getOrdersByUser(jwt)).build();
    }

    @POST
    @Operation(summary = "Create a new order",
               description = "Creates a new order for the authenticated user.")
    @RequestBody(
        description = "Details for the new order, including a list of product items.",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = CreateOrderDTO.class)
        )
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = OrderInfoDTO.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Bad Request - Invalid input or missing order items.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        ),
        @APIResponse(
            responseCode = "404",
            description = "Not Found - One or more products in the order not found.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
        )
    })
    public Response createOrder(@Valid CreateOrderDTO createOrder) {
        return Response.status(Response.Status.CREATED).entity(ordersService.createOrder(jwt, createOrder)).build();
    }
}
