package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ItemMapper {
    ItemDtoResponse toItemDtoResponse(Item item);

    Item toItemEntity(ItemDto itemDto);
}
