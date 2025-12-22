package org.example.storeapplication.services;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.entities.Item;
import org.example.storeapplication.exception.ItemNotAvailableException;
import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.models.AddToCartRequest;
import org.example.storeapplication.models.CartContent;
import org.example.storeapplication.models.CartEntity;
import org.example.storeapplication.repositories.ItemRepository;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.*;

@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class ShoppingCart {

    private final ItemRepository itemRepository;
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
                notAvailableItems.add(new CartEntity(null, id, item.getName(), quantity, null));
                return;
            }

            availableItems.add(CartEntity.builder()
                            .ordinal(availableItems.size()+1)
                            .itemId(id)
                            .itemName(item.getName())
                            .quantity(quantity)
                            .subtotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build());
        });

        BigDecimal total = availableItems.stream().map(CartEntity::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartContent(availableItems, notAvailableItems, notExistingItems, total);
    }

    public void clearCart(){
        items.clear();
    }
}
