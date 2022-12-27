package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDtoResponse getById(Long requestId, Long userId);

    List<ItemRequestDtoResponse> findOwnRequests(Long userId);

    List<ItemRequestDtoResponse> getAllRequests(Long userId, int from, int size);
}
