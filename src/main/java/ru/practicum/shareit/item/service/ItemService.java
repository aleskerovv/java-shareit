package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Long userId);

    ItemDto getById(Long itemId);

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId) throws IllegalAccessException;

    List<ItemDto> findByParams(String params);
}
