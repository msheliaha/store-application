package org.example.storeapplication.services;

import org.example.storeapplication.models.OrderDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {

    List<OrderDTO> getAllOrders(String email);

    Optional<OrderDTO> getOrderById(UUID orderId);

}
