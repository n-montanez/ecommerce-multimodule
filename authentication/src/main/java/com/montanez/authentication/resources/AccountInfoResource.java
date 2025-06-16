package com.montanez.authentication.resources;

import java.util.Set;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.montanez.authentication.models.api.ErrorResponse;
import com.montanez.authentication.models.auth.CreateCustomer;
import com.montanez.authentication.models.customer.dto.CustomerInfo;
import com.montanez.authentication.services.AccountsService;

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
    @Claim("groups")
    private Set<String> roles;

    @Inject
    JsonWebToken jwt;

    @Inject
    AccountsService accountsService;

    @GET
    @RolesAllowed({ "admin" })
    public Response getAllAcounts() {
        return Response.ok(accountsService.getAllCustomers()).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response getCustomerProfile(@PathParam("id") Long id) {
        if (!roles.contains("admin") && Long.parseLong(jwt.getSubject()) != id) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorResponse("You don't have access to this account", 403))
                    .build();
        }

        CustomerInfo result = accountsService.getCustomerInfo(id);
        return Response.ok(result).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({ "admin", "user" })
    public Response getAuthedProfile() {
        return Response
                .ok(accountsService.getCustomerInfo(Long.parseLong(jwt.getSubject())))
                .build();
    }

    @PUT
    @Path("/me")
    @RolesAllowed({ "admin", "user" })
    public Response updateAuthedProfile(CreateCustomer customer) {
        return Response
                .ok(accountsService.updateCustomerInfo(Long.parseLong(jwt.getSubject()), customer))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin" })
    public Response updateUserProfile(@PathParam("id") Long id, CreateCustomer customer) {
        return Response
                .ok(accountsService.updateCustomerInfo(id, customer))
                .build();
    }
}
