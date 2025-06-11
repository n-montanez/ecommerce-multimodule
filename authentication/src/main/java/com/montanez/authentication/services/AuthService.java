package com.montanez.authentication.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.montanez.authentication.data.CustomerDao;
import com.montanez.authentication.exceptions.auth.InvalidCredentialsException;
import com.montanez.authentication.exceptions.auth.UserNotRegisteredException;
import com.montanez.authentication.models.auth.CreateCustomer;
import com.montanez.authentication.models.auth.LoginRequest;
import com.montanez.authentication.models.auth.LoginResponse;
import com.montanez.authentication.models.customer.Customer;
import com.montanez.authentication.models.customer.dto.CustomerInfo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class AuthService {
    @Inject
    CustomerDao customerDao;

    @Inject
    JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private static final long TOKEN_EXPIRATION_TIME = 86400000; // 1 day in Milliseconds

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            Customer customer = customerDao.readCustomerByEmail(email);

            if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPasswordHash()))
                throw new InvalidCredentialsException("Invalid credentials");

            String token = jwtService.generateToken(customer, TOKEN_EXPIRATION_TIME);
            return new LoginResponse(
                    token,
                    String.valueOf((System.currentTimeMillis() + TOKEN_EXPIRATION_TIME)));
        } catch (NoResultException ex) {
            throw new UserNotRegisteredException("User is not registered");
        }
    }

    public CustomerInfo register(CreateCustomer customer) {
        Customer newCustomer = Customer.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .passwordHash(passwordEncoder.encode(customer.getPassword()))
                .dob(customer.getDob())
                .build();

        customerDao.createCustomer(newCustomer);

        return CustomerInfo.builder()
                .id(newCustomer.getId())
                .firstName(newCustomer.getFirstName())
                .lastName(newCustomer.getLastName())
                .email(newCustomer.getEmail())
                .dob(newCustomer.getDob())
                .build();
    }
}
