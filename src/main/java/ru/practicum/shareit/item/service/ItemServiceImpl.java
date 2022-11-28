package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapperTest;

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return itemRepository.getAllItems(userId).stream()
                .map(itemMapperTest::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemMapperTest.toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        Item item = itemMapperTest.toItemEntity(itemDto, userId);
        return itemMapperTest.toItemDto(itemRepository.create(item, userId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        this.checkItemsOwner(itemId, userId);

        Item item = itemMapperTest.toItemEntity(itemDto, userId);

        return itemMapperTest.toItemDto(itemRepository.update(item, itemId));
    }

    @Override
    public List<ItemDto> findByParams(String params) {
        return itemRepository.findByParams(params).stream()
                .map(itemMapperTest::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkItemsOwner(Long itemId, Long userId) {
        if (!Objects.equals(itemRepository.getById(itemId).getOwner().getId(), userId)) {
            throw new NoAccessException("You have no access to edit this item");
        }
    }
}
