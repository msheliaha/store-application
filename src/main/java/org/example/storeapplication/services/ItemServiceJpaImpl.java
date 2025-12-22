package org.example.storeapplication.services;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.mappers.ItemMapper;
import org.example.storeapplication.models.ItemDTO;
import org.example.storeapplication.repositories.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemServiceJpaImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public Page<ItemDTO> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).map(itemMapper::itemToItemDto);
    }

    @Override
    public Optional<ItemDTO> getItemById(UUID id) {
        return Optional.ofNullable(itemMapper.itemToItemDto(
                itemRepository.findById(id).orElseThrow(NotFoundException::new)));
    }
}
