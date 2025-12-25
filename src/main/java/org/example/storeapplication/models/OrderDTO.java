package org.example.storeapplication.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import org.example.storeapplication.entities.OrderItem;
import org.example.storeapplication.entities.OrderStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDTO {
    private UUID id;

    private List<OrderItemDTO> orderItems;

    @NotBlank
    private String userEmail;

    @NotNull
    private OrderStatus orderStatus;

    private BigDecimal total;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
