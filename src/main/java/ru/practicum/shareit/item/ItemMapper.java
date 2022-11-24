package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName())
                .setDescription(item.getDescription())
                .setAvailable(item.getIsAvailable())
                .setRequestId(item.getRequestId() != null ? item.getRequestId() : null)
                .setId(item.getId());

        return itemDto;
    }

    public static Item toItemEntity(ItemDto itemDto) {
        Item item = new Item();
        item.setIsAvailable(itemDto.getAvailable())
                .setDescription(itemDto.getDescription())
                .setName(itemDto.getName());

        return item;
    }

    public static Item toItemEntity(ItemDto itemDto, Long id) {
        Item item = new Item();

        item.setName(itemDto.getName())
                .setDescription(itemDto.getDescription())
                .setIsAvailable(itemDto.getAvailable())
                .setId(id)
                .setRequestId(itemDto.getRequestId());

        return item;
    }
}
