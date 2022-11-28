package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> getAllItems(Long userId);

    Item getById(Long itemId);

    Item create(Item item, Long userId);

    Item update(Item item, Long itemId);

    List<Item> findByParams(String params);
}
