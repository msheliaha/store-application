package org.example.storeapplication.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AddToCartRequest {

    @NotNull
    private UUID id;

    @Min(1)
    private Integer quantity;
}
