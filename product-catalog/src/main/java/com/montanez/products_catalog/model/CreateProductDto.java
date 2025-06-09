package com.montanez.products_catalog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDto {
    @NotBlank(message = "Product must have a name")
    @Size(max = 128, message = "Product name cannot exceed 128 characters")
    private String name;

    @NotBlank(message = "Product must have a brand")
    private String brand;

    @PositiveOrZero(message = "Product prize cannot be negative")
    private double price;
}
