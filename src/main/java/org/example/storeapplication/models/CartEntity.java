package org.example.storeapplication.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CartEntity(
        Integer ordinal,
        UUID itemId,
        String itemName,
        Integer quantity,
        BigDecimal subtotal
) {}
