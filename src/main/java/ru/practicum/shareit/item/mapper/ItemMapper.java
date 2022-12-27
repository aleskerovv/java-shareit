package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInform;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemRequestMapper.class})
public interface ItemMapper {
    @Mapping(target = "requestId", source = "itemRequest.id")
    ItemDtoResponse toItemDtoResponse(Item item);

    @Mapping(target = "itemRequest", source = "requestId", qualifiedByName = "toRequest")
    Item toItemEntity(ItemDto itemDto);

    @Mapping(target = "requestId", source = "itemRequest.id")
    ItemDtoInform toItemDtoInform(Item item);

    ItemDto toItemDto(Item item);

    @Named("toRequest")
    ItemRequest map(Long id);
}
