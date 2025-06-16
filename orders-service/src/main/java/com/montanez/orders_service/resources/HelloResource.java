package com.montanez.orders_service.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Path("/hello")
public class HelloResource {
    @GET
    public Response hello() {
        return Response.ok("Hello, world").build();
    }
}
