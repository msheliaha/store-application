package org.example.storeapplication.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private ItemDTO item;
    private Integer quantity;

    private BigDecimal price;
}
