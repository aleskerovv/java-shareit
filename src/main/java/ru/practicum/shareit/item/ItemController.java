package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.getAllItems(id);
    }

    @PostMapping
    public ItemDto createItem(@Validated(Create.class)
                              @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.create(itemDto, id);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@Validated(Update.class)
                              @RequestBody ItemDto itemDto, @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.update(itemDto, itemId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByParams(@RequestParam(value = "text", required = true) String text) {
        return itemService.findByParams(text);
    }
}
