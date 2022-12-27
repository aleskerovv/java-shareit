package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.mapper.BaseMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> getItemsByOwnerId(Long userId, Pageable pageable);

    @Query("SELECT it FROM Item it where it.available = true " +
            "and (lower(it.name) like lower(concat('%', :params,'%')) " +
            "or lower(it.description) like lower(concat('%', :params,'%')))")
    List<Item> getItemsByParams(String params, Pageable pageable);

    List<Item> getItemsByItemRequestId(Long requestId);

    @Query("SELECT it FROM Item it " +
            "where it.itemRequest.id in (:requestsId)")
    List<Item> getItemsByRequestIdList(List<Long> requestsId);

    @BaseMapper
    Item getItemById(Long id);
}
