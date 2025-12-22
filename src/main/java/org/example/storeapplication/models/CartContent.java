package org.example.storeapplication.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartContent {
    private List<CartEntity> availableItems;
    private List<CartEntity> notAvailableItems;
    private List<CartEntity> notExistingItems;
    private BigDecimal total;
}
