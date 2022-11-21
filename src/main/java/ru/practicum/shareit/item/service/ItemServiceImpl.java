package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return itemDao.getAllItems(userId);
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemDao.getById(itemId);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        return itemDao.create(itemDto, userId);
    }

    @SneakyThrows
    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        return itemDao.update(itemDto, itemId, userId);
    }

    @Override
    public List<ItemDto> findByParams(String params) {
        return itemDao.findByParams(params);
    }
}
