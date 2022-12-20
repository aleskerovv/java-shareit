package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;
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
    public ItemDtoResponse getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.getById(itemId, id);
    }

    @GetMapping
    public List<ItemDtoResponse> getAllItems(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @RequestParam(required = false, defaultValue = "0") int from,
                                             @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.getItemsByOwnerId(id, from, size);
    }

    @PostMapping
    public ItemDtoResponse createItem(@Validated(Create.class)
                              @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.create(itemDto, id);
    }

    @PatchMapping("{itemId}")
    public ItemDtoResponse updateItem(@Validated(Update.class)
                              @RequestBody ItemDto itemDto, @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.update(itemDto, itemId, id);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemsByParams(@RequestParam(value = "text") String text,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.findByParams(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDtoResponse addComment(@Valid @RequestBody CommentDto commentDto,
                                         @RequestHeader("X-Sharer-User-Id") Long id,
                                         @PathVariable Long itemId) {
        return itemService.addComment(commentDto, id, itemId);
    }
}
