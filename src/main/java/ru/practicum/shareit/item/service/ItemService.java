package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    List<ItemDtoResponse> getItemsByOwnerId(Long userId);

    ItemDtoResponse getById(Long itemId);

    ItemDtoResponse create(ItemDto itemDto, Long userId);

    ItemDtoResponse update(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDtoResponse> findByParams(String params);
}
