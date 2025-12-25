package org.example.storeapplication.mappers;

import org.example.storeapplication.entities.Order;
import org.example.storeapplication.entities.OrderItem;
import org.example.storeapplication.models.OrderDTO;
import org.example.storeapplication.models.OrderItemDTO;
import org.mapstruct.Mapper;

@Mapper(uses = ItemMapper.class)
public interface OrderMapper {

    OrderDTO orderToOrderDto(Order order);

    OrderItemDTO orderItemToOrderItemDto(OrderItem orderItem);
}
