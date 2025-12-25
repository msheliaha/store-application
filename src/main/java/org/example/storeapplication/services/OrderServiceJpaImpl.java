package org.example.storeapplication.services;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.entities.Order;
import org.example.storeapplication.entities.OrderItem;
import org.example.storeapplication.entities.OrderStatus;
import org.example.storeapplication.exception.BadRequestException;
import org.example.storeapplication.exception.ForbiddenException;
import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.mappers.OrderMapper;
import org.example.storeapplication.models.OrderDTO;
import org.example.storeapplication.repositories.ItemRepository;
import org.example.storeapplication.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public OrderDTO cancelOrder(UUID orderId, String email) {
        Order order = orderRepository.findById(orderId).orElseThrow(NotFoundException::new);
        if(!order.getUserEmail().equals(email)){
            throw new ForbiddenException();
        }
        if(order.getOrderStatus().equals(OrderStatus.CANCELED)){
            throw new BadRequestException("Cannot cancel order with status " + order.getOrderStatus());
        }
        order.setOrderStatus(OrderStatus.CANCELED);
        returnItems(order);
        return orderMapper.orderToOrderDto(order);
    }

    private void returnItems(Order order){
        List<OrderItem> orderItems = order.getOrderItems();

        orderItems.forEach(orderItem -> {
            int quantity = orderItem.getItem().getAvailable()+orderItem.getQuantity();
            orderItem.getItem().setAvailable(quantity);
        });
    }
}
