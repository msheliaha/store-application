package org.example.storeapplication.services;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.mappers.OrderMapper;
import org.example.storeapplication.models.OrderDTO;
import org.example.storeapplication.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceJpaImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderDTO> getAllOrders(String email) {
        return orderRepository.findAllByUserEmail(email).stream().map(orderMapper::orderToOrderDto).toList();
    }

    @Override
    public Optional<OrderDTO> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).map(orderMapper::orderToOrderDto);
    }
}
