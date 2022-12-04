package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public List<ItemDtoResponse> getItemsByOwnerId(Long userId) {
        return itemRepository.getItemsByOwnerId(userId).stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoResponse getById(Long itemId) {
        return itemMapper.toItemDtoResponse(itemRepository.getReferenceById(itemId));
    }

    @Override
    public ItemDtoResponse create(ItemDto itemDto, Long userId) {
        Item item = itemMapper.toItemEntity(itemDto, userId);
        item.setOwner(userMapper.toUserEntity(userService.getUserById(userId)));
        return itemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    public ItemDtoResponse update(ItemDto itemDto, Long itemId, Long userId) {
        this.checkItemsOwner(itemId, userId);

        Item item = itemRepository.getReferenceById(itemId);

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription()
                : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable()
                : item.getAvailable());

        return itemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    public List<ItemDtoResponse> findByParams(String params) {
        if (params.isEmpty() || params.isBlank()) return new ArrayList<>();

        return itemRepository.getItemsByParams(params.toLowerCase())
                .stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }

    private void checkItemsOwner(Long itemId, Long userId) {
        if (!Objects.equals(itemRepository.getReferenceById(itemId).getOwner().getId(), userId)) {
            throw new NoAccessException("You have no access to edit this item");
        }
    }
}
