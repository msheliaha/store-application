package org.example.storeapplication.services;

import org.example.storeapplication.models.ItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ItemService {

    Page<ItemDTO> getAllItems(Pageable pageable);

    Optional<ItemDTO> getItemById(UUID id);
}
