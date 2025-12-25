package org.example.storeapplication.services;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.entities.Item;
import org.example.storeapplication.entities.Order;
import org.example.storeapplication.entities.OrderItem;
import org.example.storeapplication.entities.OrderStatus;
import org.example.storeapplication.exception.ItemNotAvailableException;
import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.models.AddToCartRequest;
import org.example.storeapplication.models.CartContent;
import org.example.storeapplication.models.CartEntity;
import org.example.storeapplication.repositories.ItemRepository;
import org.example.storeapplication.repositories.OrderRepository;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.*;

@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class ShoppingCart {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final Map<UUID, Integer> items = new HashMap<>();


    public void addToCart(AddToCartRequest addToCartRequest){
        Optional<Item> found = itemRepository.findById(addToCartRequest.getId());

        if(found.isEmpty()){
            throw new NotFoundException("Item not found");
        }
        if(found.get().getAvailable()<addToCartRequest.getQuantity()){
            throw new ItemNotAvailableException("Max quantity of this item: "+found.get().getAvailable());
        }

        items.put(addToCartRequest.getId(), addToCartRequest.getQuantity());
    }

    public void removeFromCart(UUID itemId){
        if(!items.containsKey(itemId)){
            throw new NotFoundException();
        }

        items.remove(itemId);
    }

    public CartContent getCart(){
        List<CartEntity> availableItems = new ArrayList<>();
        List<CartEntity> notAvailableItems = new ArrayList<>();
        List<CartEntity> notExistingItems = new ArrayList<>();

        items.forEach((id, quantity) -> {
            Optional<Item> itemOpt = itemRepository.findById(id);
            if(itemOpt.isEmpty()) {
                notExistingItems.add(CartEntity.builder().itemId(id).build());
                return;
            }

            Item item = itemOpt.get();
            if(item.getAvailable()<quantity){
                notAvailableItems.add(CartEntity.builder()
                                .itemId(id)
                                .itemName(item.getName())
                                .quantity(quantity)
                                .price(item.getPrice())
                                .subtotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)))
                        .build());
                return;
            }

            availableItems.add(CartEntity.builder()
                            .ordinal(availableItems.size()+1)
                            .itemId(id)
                            .itemName(item.getName())
                            .quantity(quantity)
                            .price(item.getPrice())
                            .subtotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build());
        });

        BigDecimal total = availableItems.stream().map(CartEntity::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartContent(availableItems, notAvailableItems, notExistingItems, total);
    }

    public void clearCart(){
        items.clear();
    }

    @Transactional
    public void checkout(String userEmail){

        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setOrderStatus(OrderStatus.NEW);

        items.entrySet().stream()
                .map(entry->addItemToOrder(entry.getKey(),entry.getValue()))
                .forEach(order::addItem);

        BigDecimal total = order.getOrderItems().stream()
                .map(item ->item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        orderRepository.save(order);
        clearCart();
    }

    private OrderItem addItemToOrder(UUID itemId, Integer quantity){
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotAvailableException::new);
        if(item.getAvailable()<quantity){
            throw new ItemNotAvailableException();
        }

        item.setAvailable(item.getAvailable()-quantity);

        return OrderItem.builder()
                .item(item)
                .quantity(quantity)
                .price(item.getPrice())
                .build();
    }
}
