package com.montanez.authentication.resources;

import java.util.Set;

import org.eclipse.microprofile.jwt.Claim;
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
@Tag(name = "Account Management", description = "Operations related to user accounts and profiles.")
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
    @Operation(summary = "Get all customer accounts", description = "Retrieves a list of all customer accounts. Requires 'admin' role.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully retrieved all accounts", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CustomerInfo.class, type = SchemaType.ARRAY))),
            @APIResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "403", description = "Forbidden - User does not have 'admin' role", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"Forbidden\",\"errorCode\":403}"))),
    })
    public Response getAllAcounts() {
        return Response.ok(accountsService.getAllCustomers()).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Operation(summary = "Get customer profile by ID", description = "Retrieves a specific customer's profile. " +
            "Requires 'admin' role or 'user' role if the ID matches the authenticated user's ID (subject).")
    @Parameter(name = "id", description = "ID of the customer to retrieve", required = true, example = "123")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully retrieved customer profile", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CustomerInfo.class))),
            @APIResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "403", description = "Forbidden - User does not have 'admin' role and ID does not match authenticated user", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"You don't have access to this account\",\"errorCode\":403}"))),
            @APIResponse(responseCode = "404", description = "Not Found - Account not found for the given ID", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"Account not found\",\"errorCode\":404}"))),
            @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "Get authenticated user's profile", description = "Retrieves the profile of the currently authenticated user based on their JWT.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully retrieved authenticated user's profile", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CustomerInfo.class))),
            @APIResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Not Found - Authenticated account not found (unlikely but possible if data mismatch)", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"Account not found\",\"errorCode\":404}"))),
    })
    public Response getAuthedProfile() {
        return Response
                .ok(accountsService.getCustomerInfo(Long.parseLong(jwt.getSubject())))
                .build();
    }

    @PUT
    @Path("/me")
    @RolesAllowed({ "admin", "user" })
    @Operation(summary = "Update authenticated user's profile", description = "Updates the profile information of the currently authenticated user.")
    @RequestBody(description = "Updated customer details", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CreateCustomer.class)))
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully updated authenticated user's profile", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CustomerInfo.class))),
            @APIResponse(responseCode = "400", description = "Bad Request - Invalid input or validation errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"Invalid email format\",\"errorCode\":400}"))),
            @APIResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Not Found - Authenticated account not found (unlikely data mismatch)", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"Account not found\",\"errorCode\":404}")))
    })
    public Response updateAuthedProfile(CreateCustomer customer) {
        return Response
                .ok(accountsService.updateCustomerInfo(Long.parseLong(jwt.getSubject()), customer))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin" })
    @Operation(summary = "Update any customer's profile by ID (Admin only)", description = "Updates the profile information for a specific customer. Requires 'admin' role.")
    @Parameter(name = "id", description = "ID of the customer to update", required = true, example = "123")
    @RequestBody(description = "Updated customer details", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CreateCustomer.class)))
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully updated customer profile", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CustomerInfo.class))),
            @APIResponse(responseCode = "400", description = "Bad Request - Invalid input or validation errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "403", description = "Forbidden - User does not have 'admin' role", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Not Found - Account not found for the given ID", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"Account not found\",\"errorCode\":404}")))
    })
    public Response updateUserProfile(@PathParam("id") Long id, CreateCustomer customer) {
        return Response
                .ok(accountsService.updateCustomerInfo(id, customer))
                .build();
    }
}
