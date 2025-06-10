package com.montanez.authentication.models.customer.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo implements Serializable {

    private Long id;
    private String email;
    private String firtName;
    private String lastName;
    private LocalDate dob;
}
