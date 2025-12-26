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
    public static final String CART_ADD_PATH = CART_PATH+"/add";
    public static final String CART_UPDATE_PATH = CART_PATH+"/update";
    public static final String CART_PATH_ID = CART_PATH+"/{itemId}";
    public static final String CART_PATH_CHECKOUT = CART_PATH+"checkout";


    private final ShoppingCart shoppingCart;

    @PostMapping(CART_ADD_PATH)
    public ResponseEntity addToCart(@RequestBody @Validated AddToCartRequest request) {

        shoppingCart.addToCart(request);

        return ResponseEntity.ok().build();
    }

    @PutMapping(CART_UPDATE_PATH)
    public ResponseEntity updateItemInCart(@RequestBody @Validated AddToCartRequest request) {

        shoppingCart.putInCart(request);

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
