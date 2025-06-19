package com.montanez.orders_service.client;


import java.util.UUID;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.montanez.orders_service.model.product.Product;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

// TODO : Figure how to load this url from properties
@Path("/catalog")
@RegisterRestClient(baseUri = "http://localhost:8080/api/products", configKey = "pricingClient")
@Consumes("application/json")
public interface PricingClient extends AutoCloseable {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Product getProductById(@PathParam("id") UUID id);

}
