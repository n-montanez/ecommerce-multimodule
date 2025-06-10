package com.montanez.authentication.models.auth;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomer implements Serializable {
    @Email(message = "Email not valid")
    private String email;

    @Size(max = 128, message = "First Name size too long")
    private String firtName;

    @Size(max = 128, message = "Last Name size too long")
    private String lastName;

    @Past(message = "Birthday must be past")
    private LocalDate dob;

    @Size(min = 8, message = "Password is too weak")
    private String password;
}
