package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestGatewayDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;
    private static final String SHARER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody RequestGatewayDto requestDto,
                                                @RequestHeader(SHARER_ID) long userId) {
        log.info("creating new request by user with id {}", userId);
        return requestClient.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersRequests(@RequestHeader(SHARER_ID) long userId) {
        log.info("getting requests by user with id {}", userId);
        return requestClient.getUsersRequests(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(SHARER_ID) long userId,
                                                  @PathVariable long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(SHARER_ID) long userId,
                                                  @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                  @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("getting all requests by user with id {}", userId);
        return requestClient.getAllRequests(userId, from, size);
    }
}
