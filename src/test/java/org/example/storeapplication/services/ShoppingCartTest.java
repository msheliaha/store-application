package org.example.storeapplication.services;

import org.example.storeapplication.entities.Item;
import org.example.storeapplication.entities.Order;
import org.example.storeapplication.exception.ItemNotAvailableException;
import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.models.AddToCartRequest;
import org.example.storeapplication.models.CartContent;
import org.example.storeapplication.repositories.ItemRepository;
import org.example.storeapplication.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ShoppingCart shoppingCart;

    private UUID itemId;
    private Item item;

    @BeforeEach
    void setUp() {

        itemId = UUID.randomUUID();
        item = new Item();
        item.setId(itemId);
        item.setName("Test Product");
        item.setPrice(BigDecimal.valueOf(100.00));
        item.setAvailable(10);

        shoppingCart.clearCart();
    }

    @Test
    void addToCart_Success() {

        AddToCartRequest request = new AddToCartRequest(itemId, 2);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));


        shoppingCart.addToCart(request);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        CartContent content = shoppingCart.getCart();

        assertEquals(1, content.getAvailableItems().size());
        assertEquals(2, content.getAvailableItems().get(0).quantity());
    }

    @Test
    void addToCart_ItemNotFound_ShouldThrowsException() {

        AddToCartRequest request = new AddToCartRequest(itemId, 1);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> shoppingCart.addToCart(request));
    }

    @Test
    void addToCart_NotEnoughStock_ShouldThrowsException() {
        AddToCartRequest request = new AddToCartRequest(itemId, 11);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class, () -> shoppingCart.addToCart(request));
    }

    @Test
    void getCart_CalculatesTotalsAndCategorizesCorrectly() {

        UUID notAvailableId = UUID.randomUUID();
        UUID deletedId = UUID.randomUUID();

        Item notAvailableItem = Item.builder().id(notAvailableId).name("notAvailableItem").price(BigDecimal.TEN).available(5).build();
        Item deletedItem = Item.builder().id(notAvailableId).name("Deleted Item").price(BigDecimal.TEN).available(5).build();


        when(itemRepository.findById(notAvailableId)).thenReturn(Optional.of(notAvailableItem));
        when(itemRepository.findById(deletedId)).thenReturn(Optional.of(deletedItem));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));


        shoppingCart.addToCart(new AddToCartRequest(itemId, 5));
        shoppingCart.addToCart(new AddToCartRequest(notAvailableId, 5));
        shoppingCart.addToCart(new AddToCartRequest(deletedId, 5));


        notAvailableItem.setAvailable(3);

        reset(itemRepository);
        when(itemRepository.findById(notAvailableId)).thenReturn(Optional.of(notAvailableItem));
        when(itemRepository.findById(deletedId)).thenReturn(Optional.empty());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));


        CartContent cart = shoppingCart.getCart();

        assertEquals(1, cart.getAvailableItems().size());
        assertEquals(1, cart.getNotAvailableItems().size());
        assertEquals(1, cart.getNotExistingItems().size());


        assertEquals(BigDecimal.valueOf(500.0), cart.getTotal());
    }


    @Test
    void checkout_Success_ShouldCreateOrderAndReduceStock() {

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        shoppingCart.addToCart(new AddToCartRequest(itemId, 2));

        shoppingCart.checkout("user@email.com");


        assertEquals(8, item.getAvailable());

        verify(orderRepository).save(any(Order.class));

        assertTrue(shoppingCart.getCart().getAvailableItems().isEmpty());
    }

    @Test
    void checkout_WhenItemNotAvailable_ShouldThrowException() {

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        shoppingCart.addToCart(new AddToCartRequest(itemId, 2));

        item.setAvailable(1);

        assertThrows(ItemNotAvailableException.class, () ->
                shoppingCart.checkout("user@email.com")
        );

        verify(orderRepository, never()).save(any());
    }
}