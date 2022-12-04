package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.mapper.BaseMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> getItemsByOwnerId(Long userId);

    @Query("SELECT it FROM Item it where lower(it.name) like %:params% or lower(it.description) like %:params% " +
            "and it.available = TRUE")
    List<Item> getItemsByParams(String params);

    @BaseMapper
    Item getItemById(Long id);
}
