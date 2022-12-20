package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDtoResponse createNewRequest(@Valid @RequestBody ItemRequestDto requestDto,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.createItemRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findOwnRequests(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoResponse getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        return itemRequestService.getById(requestId, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(required = false, defaultValue = "0") int from,
                                                       @RequestParam(required = false, defaultValue = "10") int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }
}
