package org.example.storeapplication.services;

import org.example.storeapplication.entities.Item;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ItemService {

    Page<Item> getAllItems();

    Page<Item> getItemById(UUID id);
}
