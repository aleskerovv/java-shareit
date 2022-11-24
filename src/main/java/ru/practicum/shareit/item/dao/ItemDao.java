package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    List<Item> getAllItems(Long userId);

    Item getById(Long itemId);

    Item create(Item item, Long userId);

    Item update(Item item, Long userId);

    List<Item> findByParams(String params);
}
