package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentGatewayDto;
import ru.practicum.shareit.item.dto.ItemGatewayDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String SHARER_ID = "X-Sharer-User-Id";

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId, @RequestHeader(SHARER_ID) long userId) {
        log.info("getting item by id {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(SHARER_ID) long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("getting all items by user with id {}", userId);
        return itemClient.getAllItems(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(Create.class)
                                             @RequestBody ItemGatewayDto itemDto,
                                             @RequestHeader(SHARER_ID) long userId) {
        log.info("creating new item: {}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@Validated(Update.class)
                                             @RequestBody ItemGatewayDto itemDto,
                                             @PathVariable long itemId,
                                             @RequestHeader(SHARER_ID) long userId) {
        log.info("updating item with id {}", itemId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByParams(@RequestParam(value = "text") String text,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("getting items by parameters {}", text);
        return itemClient.getItemsByParams(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentGatewayDto commentDto,
                                             @RequestHeader(SHARER_ID) long userId,
                                             @PathVariable long itemId) {
        log.info("adding new comment by user with id {}", userId);
        return itemClient.addComment(commentDto, userId, itemId);
    }
}
