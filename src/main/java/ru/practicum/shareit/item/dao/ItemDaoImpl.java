package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {

    private final Map<Long, Item> itemRepository = new HashMap<>();
    private final UserDao userDao;

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        log.info("Found items whose owner is user with id {}", userId);
        return itemRepository.values().stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.get(itemId));
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        Item item = new Item();
        item.setName(itemDto.getName())
                .setDescription(itemDto.getDescription())
                .setIsAvailable(itemDto.getAvailable())
                .setId(incrementId())
                .setOwner(userDao.getById(userId));

        itemRepository.put(item.getId(), item);
        log.info("Created new item. ItemID: {}", item.getId());

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId,Long userId) {
        UserDto user = userDao.getById(userId);
        Item item = itemRepository.get(itemId);

        if (!item.getOwner().equals(user)) {
            throw new NoAccessException("You have no access to edit this item");
        }

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setIsAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getIsAvailable());

        itemRepository.put(item.getId(), item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findByParams(String params) {
        if (params.isEmpty() || params.isBlank()) return new ArrayList<>();

        return itemRepository.values().stream()
                .map(ItemMapper::toItemDto)
                .filter(v -> v.getName().toLowerCase().contains(params.toLowerCase()) ||
                        v.getDescription().toLowerCase().contains(params.toLowerCase()))
                .filter(ItemDto::getAvailable)
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
