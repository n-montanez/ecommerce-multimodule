package com.montanez.authentication.models.customer;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
@NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c")
@NamedQuery(name = "Customer.findByEmail", query = "SELECT c FROM Customer c WHERE " +
        "c.email = :email")
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email not valid")
    @Column(unique = true)
    private String email;

    @Size(max = 128, message = "First Name size too long")
    private String firstName;

    @Size(max = 128, message = "Last Name size too long")
    private String lastName;

    @Past(message = "Birthday must be past")
    private LocalDate dob;

    @Size(max = 60, message = "Password was not properly hashed")
    private String passwordHash;

    @Default
    private String role = "user";
}
