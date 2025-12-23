package org.example.storeapplication.controllers;

import org.example.storeapplication.models.OrderDTO;
import org.example.storeapplication.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.example.storeapplication.controllers.OrderController.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(username = "user@test.com")
    void getOrders_ShouldReturnList() throws Exception {

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserEmail("user@test.com");

        when(orderService.getAllOrders("user@test.com")).thenReturn(List.of(orderDTO));

        mockMvc.perform(get(ORDER_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1));

        verify(orderService).getAllOrders("user@test.com");
    }

    @Test
    void getOrders_WhenUnAuthorized_ShouldReturn401() throws Exception {
        mockMvc.perform(get(ORDER_PATH))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = "owner@test.com")
    void getOrderById_WhenOwner_ShouldResponseOk() throws Exception {

        UUID orderId = UUID.randomUUID();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderId);
        orderDTO.setUserEmail("owner@test.com");

        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(orderDTO));

        mockMvc.perform(get(ORDER_PATH + "/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    @WithMockUser(username = "hacker@test.com")
    void getOrderById_WhenNotOwner_ShouldReturnForbidden() throws Exception {

        UUID orderId = UUID.randomUUID();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderId);
        orderDTO.setUserEmail("owner@test.com");

        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(orderDTO));

        mockMvc.perform(get(ORDER_PATH + "/{orderId}", orderId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getOrderById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(orderService.getOrderById(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(get(ORDER_PATH_ID, orderId))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "user@test.com")
    void cancelOrder_ShouldReturnUpdatedOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderDTO canceledOrder = new OrderDTO();
        canceledOrder.setId(orderId);

        when(orderService.cancelOrder(orderId, "user@test.com")).thenReturn(canceledOrder);

        mockMvc.perform(put(ORDER_CANCEL_PATH_ID, orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));

        verify(orderService).cancelOrder(orderId, "user@test.com");
    }

}