package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.PageConverter.toPageRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRequestMapper requestMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requester = userMapper.toUserEntity(userService.getUserById(userId));

        ItemRequest request = requestMapper.toItemRequest(itemRequestDto).setRequester(requester);
        request.setRequester(requester);

        ItemRequestDtoResponse response = requestMapper.toRequestDtoResponse(requestRepository.save(request));
        log.info("created new item request: {}", response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDtoResponse getById(Long requestId, Long userId) {
        userService.getUserById(userId);

        try {
            ItemRequest request = requestRepository.getReferenceById(requestId);

            ItemRequestDtoResponse requestDtoResponse = requestMapper.toRequestDtoResponse(request);
            this.setItemInfoForRequests(requestDtoResponse);

            return requestDtoResponse;
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(String.format("request with id %d not found",
                    requestId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoResponse> findOwnRequests(Long userId) {
        userService.getUserById(userId);
        List<ItemRequestDtoResponse> requests = requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toRequestDtoResponse)
                .collect(Collectors.toList());
        this.setItemInfoForRequests(requests);

        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoResponse> getAllRequests(Long userId, int from, int size) {
        if (from < 0) throw new PaginationException("Page index must not be less than zero");
        if (size < 1) throw new PaginationException("Page size must not be less than one");

        Pageable pageable = toPageRequest(from, size).withSort(Sort.Direction.DESC, "created");
        List<ItemRequestDtoResponse> requests = requestRepository.findAllRequests(userId, pageable).stream()
                .map(requestMapper::toRequestDtoResponse)
                .collect(Collectors.toList());
        this.setItemInfoForRequests(requests);

        return requests;
    }

    private void setItemInfoForRequests(List<ItemRequestDtoResponse> requests) {
        if (!requests.isEmpty()) {
            List<Long> requestsIdList = requests.stream()
                    .map(ItemRequestDtoResponse::getId)
                    .collect(Collectors.toList());

            List<Item> items = itemRepository.getItemsByRequestIdList(requestsIdList);

            Map<Long, ItemRequestDtoResponse> requestsMap = requests.stream()
                    .collect(Collectors.toMap(ItemRequestDtoResponse::getId, Function.identity()));
            items.forEach(item -> Optional.ofNullable(requestsMap.get(item.getItemRequest().getId()))
                    .ifPresent(r -> r.getItems().add(itemMapper.toItemDtoInform(item))));
        }
    }

    private void setItemInfoForRequests(ItemRequestDtoResponse request) {
        List<Item> items = itemRepository.getItemsByItemRequestId(request.getId());

        items.forEach(item -> request.getItems().add(itemMapper.toItemDtoInform(item)));
    }
}
