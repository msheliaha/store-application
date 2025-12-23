package org.example.storeapplication.services;

import org.example.storeapplication.entities.Item;
import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.mappers.ItemMapper;
import org.example.storeapplication.models.ItemDTO;
import org.example.storeapplication.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceJpaImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceJpaImpl itemService;

    @Test
    void getAllItems_ShouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Item item = getTestItem();

        ItemDTO itemDTO = getTestDto();

        Page pageEntity = new PageImpl<>(List.of(item));

        when(itemRepository.findAll(pageable)).thenReturn(pageEntity);
        when(itemMapper.itemToItemDto(any())).thenReturn(itemDTO);

        Page<ItemDTO> result = itemService.getAllItems(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(itemRepository).findAll(pageable);
        verify(itemMapper).itemToItemDto(item);
    }

    @Test
    void getItemById_WhenItemExists_ShouldReturnOptionalOfDto() {
        UUID id = UUID.randomUUID();
        Item item = getTestItem();
        ItemDTO itemDTO = getTestDto();

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(itemMapper.itemToItemDto(item)).thenReturn(itemDTO);

        Optional<ItemDTO> result = itemService.getItemById(id);

        assertTrue(result.isPresent());
        assertEquals(itemDTO, result.get());
        verify(itemRepository).findById(id);
    }

    @Test
    void getItemById_WhenNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(id);
        });

        verify(itemMapper, never()).itemToItemDto(any());
    }

    ItemDTO getTestDto(){
        return ItemDTO.builder()
                .name("some name")
                .price(new BigDecimal("1.1"))
                .build();
    }

    Item getTestItem(){
        return Item.builder()
                .name("some name")
                .price(new BigDecimal("1.1"))
                .build();
    }
}
