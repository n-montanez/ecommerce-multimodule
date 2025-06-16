package com.montanez.authentication.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.montanez.authentication.models.customer.Customer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class JwtService {
    @Inject
    @ConfigProperty(name = "jwt.secret")
    private String privateKeyLocation;

    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        try {
            // Read the content of the PEM file
            String pemContent = new String(Files.readAllBytes(Paths.get(privateKeyLocation)), StandardCharsets.UTF_8);

            // Clean the PEM string: remove headers, footers, and newlines
            String base64EncodedKey = pemContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", ""); // Remove all whitespace (including newlines)

            // Decode the base64 content
            byte[] decodedKey = Base64.getDecoder().decode(base64EncodedKey);

            // Create the PKCS8EncodedKeySpec and generate the PrivateKey
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory kf = KeyFactory.getInstance("RSA"); // Specify RSA algorithm
            this.privateKey = kf.generatePrivate(keySpec);

        } catch (IOException e) {
            System.err.println("Failed to read private key file: " + e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Failed to load RSA private key: " + e.getMessage());
        }
    }

    public String generateToken(Customer customer, Long expireTime) {
        Set<String> roles = Set.of(customer.getRole());
        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .claim(Claims.SUBJECT, customer.getId().toString())
                .claim("upn", customer.getEmail())
                .claim("groups", roles.toArray(new String[roles.size()]))
                .issuer("https://github.com/n-montanez")
                .issuedAt(new Date())
                .claim(Claims.AUDIENCE, "ecommerce")
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}
