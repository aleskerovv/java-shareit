package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return itemDao.getAllItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(itemDao.getById(itemId));
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItemEntity(itemDto);
        return ItemMapper.toItemDto(itemDao.create(item, userId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toItemEntity(itemDto, itemId);
        return ItemMapper.toItemDto(itemDao.update(item, userId));
    }

    @Override
    public List<ItemDto> findByParams(String params) {
        return itemDao.findByParams(params).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
