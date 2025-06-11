package com.montanez.authentication.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.montanez.authentication.models.api.ErrorResponse;
import com.montanez.authentication.models.auth.CreateCustomer;
import com.montanez.authentication.models.auth.LoginRequest;
import com.montanez.authentication.models.auth.LoginResponse;
import com.montanez.authentication.models.customer.dto.CustomerInfo;
import com.montanez.authentication.services.AuthService;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path("/auth")
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthResource {

    @Inject
    private AuthService authService;

    @POST
    @Path("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token.")
    @RequestBody(
        description = "User login credentials",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = LoginRequest.class)
        )
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Successful login, returns JWT token and expiration",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ErrorResponse.class,
                                 example = "{\"message\":\"Invalid credentials\",\"errorCode\":401}")
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "User not registered",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ErrorResponse.class,
                                 example = "{\"message\":\"User is not registered\",\"errorCode\":404}")
            )
        )
    })
    public Response performLogin(@RequestBody LoginRequest loginRequest) {
        LoginResponse result = authService.login(loginRequest);
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account.")
    @RequestBody(
        description = "User details for registration",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = CreateCustomer.class)
        )
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "201",
            description = "User account created successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = CustomerInfo.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Bad Request (e.g., validation errors for input data)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ErrorResponse.class,
                                 example = "{\"message\":\"Invalid email format\",\"errorCode\":400}")
            )
        )
    })
    public Response createAccount(@Valid CreateCustomer customer) {
        return Response
        .status(201)
        .entity(authService.register(customer))
        .build();
    }
}
