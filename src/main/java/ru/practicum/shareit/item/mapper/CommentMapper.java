package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentMapper {
    Comment toCommentEntity(CommentDto commentDto);

    @Mapping(source = "author.name", target = "authorName")
    CommentDtoResponse toCommentDtoResponse(Comment comment);
}
