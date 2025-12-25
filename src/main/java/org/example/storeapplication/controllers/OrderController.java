package org.example.storeapplication.controllers;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.models.OrderDTO;
import org.example.storeapplication.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    public static final String ORDER_PATH="/api/v1/order";
    public static final String ORDER_PATH_ID=ORDER_PATH+"/{orderId}";
    public static final String ORDER_CANCEL_PATH_ID=ORDER_PATH_ID+"/cancel";

    private final OrderService orderService;

    @GetMapping(ORDER_PATH)
    public ResponseEntity getOrders(Principal principal){
        String email = principal.getName();

        return ResponseEntity.ok(orderService.getAllOrders(email));
    }

    @GetMapping(ORDER_PATH_ID)
    public ResponseEntity getOrderById(@PathVariable("orderId") UUID orderId, Principal principal){
        OrderDTO orderDTO = orderService.getOrderById(orderId).orElseThrow(NotFoundException::new);

        if(!orderDTO.getUserEmail().equals(principal.getName())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping(ORDER_CANCEL_PATH_ID)
    public ResponseEntity cancelOrder(@PathVariable("orderId") UUID orderId, Principal principal){
        OrderDTO orderDTO = orderService.cancelOrder(orderId, principal.getName());

        return ResponseEntity.ok(orderDTO);
    }
}
