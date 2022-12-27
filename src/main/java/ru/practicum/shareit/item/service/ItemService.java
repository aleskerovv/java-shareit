package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    List<ItemDtoResponse> getItemsByOwnerId(Long userId, int from, int size);

    ItemDtoResponse getById(Long itemId, Long userId);

    ItemDtoResponse create(ItemDto itemDto, Long userId);

    ItemDtoResponse update(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDtoResponse> findByParams(String params, int from, int size);

    CommentDtoResponse addComment(CommentDto commentDto, Long userId, Long itemId);
}
