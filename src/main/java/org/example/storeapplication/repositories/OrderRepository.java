package org.example.storeapplication.repositories;

import org.example.storeapplication.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByUserEmail(String userEmail);
}
