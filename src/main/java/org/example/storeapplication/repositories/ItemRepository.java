package org.example.storeapplication.repositories;

import org.example.storeapplication.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
}
