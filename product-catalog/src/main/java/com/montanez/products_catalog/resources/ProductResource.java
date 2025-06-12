package com.montanez.products_catalog.resources;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.montanez.products_catalog.data.ProductDao;
import com.montanez.products_catalog.model.CreateProductDto;
import com.montanez.products_catalog.model.Product;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/catalog")
@RequestScoped
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product", description = "Operations related to products in the catalog")
public class ProductResource {
    @Inject
    ProductDao productDao;

    @GET
    @Operation(summary = "Get all products", description = "Retrieves a list of all available products.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully retrieved products", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Product.class, type = SchemaType.ARRAY)))
    })
    public Response getAllProducts() {
        return Response
                .ok()
                .entity(productDao.readAllProducts())
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its unique identifier.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully retrieved product", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Product.class))),
            @APIResponse(responseCode = "404", description = "Product not found")
    })
    public Response getById(@PathParam("id") UUID id) {
        Product product = productDao.readProduct(id);

        if (product == null)
            throw new NotFoundException("Product with id " + id + " not found");

        return Response
                .ok()
                .entity(product)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new product", description = "Creates a new product with the provided details.")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Product.class))),
            @APIResponse(responseCode = "400", description = "Invalid product data provided")
    })
    @RolesAllowed({ "admin" })
    public Response createProduct(CreateProductDto productDto) {
        Product product = Product
                .builder()
                .name(productDto.getName())
                .brand(productDto.getBrand())
                .price(productDto.getPrice())
                .build();

        productDao.createProduct(product);

        return Response
                .created(null)
                .entity(product)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an existing product", description = "Updates the details of an existing product.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Product updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Product.class))),
            @APIResponse(responseCode = "404", description = "Product not found"),
            @APIResponse(responseCode = "400", description = "Invalid product data provided")
    })
    @RolesAllowed({ "admin" })
    public Response updateProduct(@PathParam("id") UUID id, CreateProductDto productDto) {
        Product product = productDao.readProduct(id);

        if (product == null)
            throw new NotFoundException("Product with ID " + id + " not found");

        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());

        productDao.updateProduct(product);
        return Response
                .status(200)
                .entity(product)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its unique identifier.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Product deleted successfully (No Content)"),
            @APIResponse(responseCode = "404", description = "Product not found (though a 204 is often returned even if not found for idempotency)")
    })
    @RolesAllowed({ "admin" })
    public Response deleteProducts(@PathParam("id") UUID id) {
        Product product = productDao.readProduct(id);

        if (product != null)
            productDao.deleteProduct(product);

        return Response.noContent().build();
    }

}
