package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("{itemId}")
    public ItemDtoResponse getItemById(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.getById(itemId, id);
    }

    @GetMapping
    public List<ItemDtoResponse> getAllItems(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @RequestParam(required = false) int from,
                                             @RequestParam(required = false) int size) {
        return itemService.getItemsByOwnerId(id, from, size);
    }

    @PostMapping
    public ItemDtoResponse createItem(@RequestBody ItemDto itemDto,
                                      @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.create(itemDto, id);
    }

    @PatchMapping("{itemId}")
    public ItemDtoResponse updateItem(@RequestBody ItemDto itemDto, @PathVariable long itemId,
                                      @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.update(itemDto, itemId, id);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemsByParams(@RequestParam(value = "text") String text,
                                                  @RequestParam(required = false) int from,
                                                  @RequestParam(required = false) int size) {
        return itemService.findByParams(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDtoResponse addComment(@RequestBody CommentDto commentDto,
                                         @RequestHeader("X-Sharer-User-Id") Long id,
                                         @PathVariable Long itemId) {
        return itemService.addComment(commentDto, id, itemId);
    }
}