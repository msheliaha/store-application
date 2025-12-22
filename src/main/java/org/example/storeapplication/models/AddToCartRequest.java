package org.example.storeapplication.models;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AddToCartRequest {

    private UUID id;

    @Min(1)
    private Integer quantity;
}
