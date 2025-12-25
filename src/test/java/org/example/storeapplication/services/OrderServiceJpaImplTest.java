package org.example.storeapplication.services;

import org.example.storeapplication.entities.Item;
import org.example.storeapplication.entities.Order;
import org.example.storeapplication.entities.OrderItem;
import org.example.storeapplication.entities.OrderStatus;
import org.example.storeapplication.exception.BadRequestException;
import org.example.storeapplication.exception.ForbiddenException;
import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.mappers.OrderMapper;
import org.example.storeapplication.models.OrderDTO;
import org.example.storeapplication.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceJpaImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceJpaImpl orderService;

    @Test
    void getAllOrders_ShouldReturnListOfDtos() {

        String email = "test@user.com";
        Order order = new Order();
        OrderDTO orderDTO = new OrderDTO();

        when(orderRepository.findAllByUserEmail(email)).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDto(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getAllOrders(email);

        assertEquals(1, result.size());
        verify(orderRepository).findAllByUserEmail(email);
    }

    @Test
    void getOrderById_WhenFound_ShouldReturnDto() {

        UUID id = UUID.randomUUID();
        Order order = new Order();
        OrderDTO dto = new OrderDTO();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderDto(order)).thenReturn(dto);

        Optional<OrderDTO> result = orderService.getOrderById(id);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void getOrderById_WhenNotFound_ShouldReturnEmpty() {

        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        Optional<OrderDTO> result = orderService.getOrderById(id);

        assertTrue(result.isEmpty());
    }


    @Test
    void cancelOrder_Success_ShouldUpdateStatusAndReturnStock() {

        UUID orderId = UUID.randomUUID();
        String userEmail = "owner@test.com";

        Item item = new Item();
        item.setAvailable(10);

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(2);

        Order order = new Order();
        order.setId(orderId);
        order.setUserEmail(userEmail);
        order.setOrderStatus(OrderStatus.NEW);
        order.setOrderItems(List.of(orderItem));

        OrderDTO expectedDto = new OrderDTO();
        expectedDto.setOrderStatus(OrderStatus.CANCELED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderDto(order)).thenReturn(expectedDto);

        OrderDTO result = orderService.cancelOrder(orderId, userEmail);


        assertNotNull(result);
        assertEquals(OrderStatus.CANCELED, result.getOrderStatus());

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());

        assertEquals(12, item.getAvailable());

        verify(orderMapper).orderToOrderDto(order);
    }

    @Test
    void cancelOrder_WhenOrderNotFound_ThrowNotFoundException() {

        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                orderService.cancelOrder(id, "some@email.com")
        );
    }

    @Test
    void cancelOrder_WhenEmailMismatch_ThrowForbiddenException() {

        UUID id = UUID.randomUUID();
        Order order = new Order();
        order.setUserEmail("owner@test.com");

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(ForbiddenException.class, () ->
                orderService.cancelOrder(id, "hacker@test.com")
        );

        assertNotEquals(OrderStatus.CANCELED, order.getOrderStatus());
    }

    @Test
    void cancelOrder_WhenAlreadyCanceled_ThrowBadRequestException() {

        UUID id = UUID.randomUUID();
        String email = "owner@test.com";
        Order order = new Order();
        order.setUserEmail(email);
        order.setOrderStatus(OrderStatus.CANCELED);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () ->
                orderService.cancelOrder(id, email)
        );
    }

}