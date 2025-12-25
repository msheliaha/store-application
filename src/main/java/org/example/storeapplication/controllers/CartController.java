package org.example.storeapplication.controllers;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.models.AddToCartRequest;
import org.example.storeapplication.models.CartContent;
import org.example.storeapplication.services.ShoppingCart;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CartController {

    public static final String CART_PATH = "/api/v1/cart";
    public static final String CART_PATH_ID = "/api/v1/cart/{itemId}";
    public static final String CART_PATH_CHECKOUT = "/api/v1/cart/checkout";


    private final ShoppingCart shoppingCart;

    @PostMapping(CART_PATH)
    public ResponseEntity addToCart(@RequestBody @Validated AddToCartRequest request) {

        shoppingCart.addToCart(request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(CART_PATH_ID)
    public ResponseEntity deleteFromCart(@PathVariable("itemId") UUID itemId){
        shoppingCart.removeFromCart(itemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(CART_PATH)
    public ResponseEntity deleteCart(){
        shoppingCart.clearCart();

        return ResponseEntity.noContent().build();
    }

    @GetMapping(CART_PATH)
    public ResponseEntity getCartContent(){
        CartContent content = shoppingCart.getCart();

        return ResponseEntity.ok(content);
    }

    @PostMapping(CART_PATH_CHECKOUT)
    public ResponseEntity checkoutOrder(Principal principal){

        String email = principal.getName();

        shoppingCart.checkout(email);

        return ResponseEntity.ok("New Order Created for " + email);
    }
}
