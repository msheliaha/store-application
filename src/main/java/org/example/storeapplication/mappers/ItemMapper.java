package org.example.storeapplication.mappers;

import org.example.storeapplication.entities.Item;
import org.example.storeapplication.models.ItemDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ItemMapper {

    Item itemDtoToItem(ItemDTO itemDTO);

    ItemDTO itemToItemDto(Item item);

}
