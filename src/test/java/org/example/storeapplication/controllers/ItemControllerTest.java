package org.example.storeapplication.controllers;

import org.example.storeapplication.exception.NotFoundException;
import org.example.storeapplication.models.ItemDTO;
import org.example.storeapplication.services.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.example.storeapplication.controllers.ItemController.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemController.class)
@WithMockUser(username = "example@mail.com")
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getAll_ShouldReturnPageOfItems() throws Exception {
        ItemDTO item1 = ItemDTO.builder().name("Item 1").price(BigDecimal.valueOf(10)).build();
        ItemDTO item2 = ItemDTO.builder().name("Item 2").price(BigDecimal.valueOf(20)).build();

        Page mockPage = new PageImpl<>(List.of(item1, item2));

        Pageable pageable = Pageable.ofSize(10).withPage(0);

        when(itemService.getAllItems(pageable)).thenReturn(mockPage);

        mockMvc.perform(get(ITEM_PATH)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Item 1"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(itemService).getAllItems(pageable);
    }

    @Test
    void getById_WhenFound_ShouldReturnItem() throws Exception {
        UUID itemId = UUID.randomUUID();
        ItemDTO itemDTO = ItemDTO.builder()
                .id(itemId)
                .name("Item")
                .price(BigDecimal.valueOf(99.99))
                .build();

        when(itemService.getItemById(itemId)).thenReturn(Optional.of(itemDTO));

        mockMvc.perform(get(ITEM_PATH_ID, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.price").value(99.99));

        verify(itemService).getItemById(itemId);
    }

    @Test
    void getById_WhenNotFound_ShouldReturnNotFound() throws Exception {

        UUID itemId = UUID.randomUUID();

        when(itemService.getItemById(itemId)).thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get(ITEM_PATH_ID, itemId))
                .andExpect(status().isNotFound());

        verify(itemService).getItemById(itemId);
    }
}