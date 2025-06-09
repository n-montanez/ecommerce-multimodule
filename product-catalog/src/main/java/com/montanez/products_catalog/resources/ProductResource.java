package com.montanez.products_catalog.resources;

import java.util.UUID;

import com.montanez.products_catalog.dao.ProductDao;
import com.montanez.products_catalog.model.CreateProductDto;
import com.montanez.products_catalog.model.Product;

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
public class ProductResource {
    @Inject
    ProductDao productDao;

    @GET
    public Response getAllProducts() {
        return Response
                .ok()
                .entity(productDao.readAllProducts())
                .build();
    }

    @GET
    @Path("/{id}")
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
    public Response deleteProducts(@PathParam("id") UUID id) {
        Product product = productDao.readProduct(id);

        if (product != null)
            productDao.deleteProduct(product);
        
        return Response.noContent().build();
    }

}
