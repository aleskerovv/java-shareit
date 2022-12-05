package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

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
    public List<ItemDtoResponse> getAllItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.getItemsByOwnerId(id);
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
    public List<ItemDtoResponse> getItemsByParams(@RequestParam(value = "text") String text) {
        return itemService.findByParams(text);
    }
}
