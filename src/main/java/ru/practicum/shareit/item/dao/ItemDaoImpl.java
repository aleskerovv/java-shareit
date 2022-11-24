package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {

    private final Map<Long, Item> itemRepository = new HashMap<>();
    private final UserDao userDao;

    @Override
    public List<Item> getAllItems(Long userId) {
        log.info("Find items whose owner is user with id {}", userId);
        return itemRepository.values().stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Long itemId) {
        if (!itemRepository.containsKey(itemId))
            throw new NotFoundException(String.format("Item with ID %d not found", itemId));

        return itemRepository.get(itemId);
    }

    @Override
    public Item create(Item item, Long userId) {
        item.setOwner(userDao.getById(userId))
                        .setId(this.incrementId());

        itemRepository.put(item.getId(), item);
        log.info("Created new item. ItemID: {}", item.getId());

        return item;
    }

    @Override
    public Item update(Item item, Long userId) {
        User user = this.userDao.getById(userId);
        Item itemToUpdate = this.itemRepository.get(item.getId());

        if (!itemToUpdate.getOwner().equals(user)) {
            throw new NoAccessException("You have no access to edit this item");
        }

        itemToUpdate.setName(item.getName() != null ? item.getName() : itemToUpdate.getName());
        itemToUpdate.setDescription(item.getDescription() != null ? item.getDescription()
                : itemToUpdate.getDescription());
        itemToUpdate.setIsAvailable(item.getIsAvailable() != null ? item.getIsAvailable()
                : itemToUpdate.getIsAvailable());

        itemRepository.put(itemToUpdate.getId(), itemToUpdate);
        log.info("Updated item with ID {}", itemToUpdate.getId());

        return itemToUpdate;
    }

    @Override
    public List<Item> findByParams(String params) {
        if (params.isEmpty() || params.isBlank()) return new ArrayList<>();

        log.info("Find items which contains substring-param: {}", params);
        return itemRepository.values().stream()
                .filter(v -> v.getName().toLowerCase().contains(params.toLowerCase()) ||
                        v.getDescription().toLowerCase().contains(params.toLowerCase()))
                .filter(Item::getIsAvailable)
                .collect(Collectors.toList());
    }

    private long incrementId() {
        Long id = itemRepository.values().stream()
                .map(Item::getId)
                .max(Long::compareTo)
                .orElse(0L);

        return id + 1;
    }
}
