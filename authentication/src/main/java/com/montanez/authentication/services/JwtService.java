package com.montanez.authentication.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.montanez.authentication.models.customer.Customer;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class JwtService {
    @Inject
    @ConfigProperty(name = "jwt.secret")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(Customer customer, Long expireTime) {
        return Jwts.builder()
                .subject(customer.getId().toString())
                .claim("groups", customer.getRole())
                .issuer("com.montanez")
                .issuedAt(new Date())
                .audience().add("e-commerce").and()
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
