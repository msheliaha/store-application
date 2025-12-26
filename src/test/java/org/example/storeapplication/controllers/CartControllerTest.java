package org.example.storeapplication.controllers;

import org.example.storeapplication.models.AddToCartRequest;
import org.example.storeapplication.models.CartContent;
import org.example.storeapplication.services.ShoppingCart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

import static org.example.storeapplication.controllers.CartController.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CartController.class)
@WithMockUser(username = "test@user.com")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ShoppingCart shoppingCart;


    @Test
    void addToCart_WhenValidRequest_ShouldResponseWithOk() throws Exception {

        UUID itemId = UUID.randomUUID();
        AddToCartRequest request = new AddToCartRequest(itemId, 2);

        mockMvc.perform(post(CART_ADD_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(shoppingCart).addToCart(request);
    }

    @Test
    void addToCart_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        AddToCartRequest request = new AddToCartRequest(UUID.randomUUID(), -5);

        mockMvc.perform(post(CART_ADD_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(shoppingCart, never()).addToCart(any());
    }

    @Test
    void updateItemInCart_WhenValidRequest_ShouldPutItemAndResponseOk() throws Exception {
        UUID itemId = UUID.randomUUID();
        AddToCartRequest request = new AddToCartRequest(itemId, 2);

        mockMvc.perform(put(CART_UPDATE_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(shoppingCart).putInCart(request);
    }

    @Test
    void deleteFromCart_ShouldResponseWithNoContent() throws Exception {
        UUID itemId = UUID.randomUUID();

        mockMvc.perform(delete(CART_PATH_ID, itemId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(shoppingCart).removeFromCart(itemId);
    }

    @Test
    void deleteCart_ShouldResponseWithNoContent() throws Exception {
        mockMvc.perform(delete(CART_PATH)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(shoppingCart).clearCart();
    }

    @Test
    void getCartContent_ShouldReturnJsonBody() throws Exception {

        CartContent mockContent = new CartContent(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                BigDecimal.ZERO
        );
        when(shoppingCart.getCart()).thenReturn(mockContent);


        mockMvc.perform(get(CART_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.total").exists());

        verify(shoppingCart).getCart();
    }

    @Test
    void checkoutOrder_ShouldResponseWithOk() throws Exception {
        String email = "test@user.com";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(email);


        mockMvc.perform(post(CART_PATH_CHECKOUT)
                        .with(csrf())
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("New Order Created for " + email));

        verify(shoppingCart).checkout(email);
    }
}