package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemDao {

    List<ItemDto> getAllItems(Long userId);

    ItemDto getById(Long itemId);

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId,Long userId) throws IllegalAccessException;

    List<ItemDto> findByParams(String params);
}
