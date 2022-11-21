package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName())
                .setDescription(item.getDescription())
                .setOwner(item.getOwner())
                .setAvailable(item.getIsAvailable())
                .setRequestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .setId(item.getId());

        return itemDto;
    }
}
