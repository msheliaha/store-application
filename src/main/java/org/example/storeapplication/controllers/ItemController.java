package org.example.storeapplication.controllers;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.services.ItemService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ItemController {

    public final static String ITEM_PATH = "/api/v1/item";
    public final static String ITEM_PATH_ID = ITEM_PATH+"/{itemId}";

    private final ItemService itemService;

    @GetMapping(ITEM_PATH)
    public ResponseEntity getAll(Pageable pageable){
        return ResponseEntity.ok().body(itemService.getAllItems(pageable));
    }

    @GetMapping(ITEM_PATH_ID)
    public ResponseEntity getById(@PathVariable("itemId") UUID itemId){

        return ResponseEntity.ok(itemService.getItemById(itemId));
    }
}
