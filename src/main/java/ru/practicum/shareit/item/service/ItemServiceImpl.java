package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoInform;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.IncorrectStateException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> getItemsByOwnerId(Long userId) {
        List<Item> items = itemRepository.getItemsByOwnerId(userId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());

        List<ItemDtoResponse> itemsDto = this.setBookingsDates(items);
        this.setCommentsToItem(itemsDto);

        return itemsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getById(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        ItemDtoResponse itemDto = this.setBookingsDates(item, userId);
        this.setCommentsToItem(itemDto);
        return itemDto;
    }

    @Override
    public ItemDtoResponse create(ItemDto itemDto, Long userId) {
        Item item = itemMapper.toItemEntity(itemDto, userId);
        item.setOwner(userMapper.toUserEntity(userService.getUserById(userId)));
        return itemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemDtoResponse update(ItemDto itemDto, Long itemId, Long userId) {
        this.checkItemsOwner(itemId, userId);

        Item item = itemRepository.getReferenceById(itemId);

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription()
                : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable()
                : item.getAvailable());

        return itemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> findByParams(String params) {
        if (params.isEmpty() || params.isBlank()) return new ArrayList<>();

        return itemRepository.getItemsByParams(params.toLowerCase())
                .stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CommentDtoResponse addComment(CommentDto commentDto, Long userId, Long itemId) {
        if (bookingRepository.findBookingsByUserAndItemId(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new IncorrectStateException("You are not have any ended booking for this item");
        }
        Comment comment = commentMapper.toCommentEntity(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(itemRepository.getItemById(itemId));
        comment.setAuthor(userMapper.toUserEntity(userService.getUserById(userId)));

        return commentMapper.toCommentDtoResponse(commentRepository.save(comment));
    }

    private void checkItemsOwner(Long itemId, Long userId) {
        if (!Objects.equals(itemRepository.getReferenceById(itemId).getOwner().getId(), userId)) {
            throw new NoAccessException("You have no access to edit this item");
        }
    }

    private void setCommentsToItem(ItemDtoResponse item) {
        Set<CommentDtoResponse> comments = commentRepository.findCommentsByItemId(item.getId()).stream()
                .map(commentMapper::toCommentDtoResponse)
                .collect(Collectors.toSet());
        item.setComments(comments);
    }

    private void setCommentsToItem(List<ItemDtoResponse> items) {
        Map<Long, ItemDtoResponse> itemsDto = new HashMap<>();
        items.forEach(item -> itemsDto.put(item.getId(), item));

        Set<Comment> comments = new HashSet<>(commentRepository.findAll());

        if (!itemsDto.isEmpty()) {
            comments.forEach(comment -> Optional.ofNullable(itemsDto.get(comment.getItem().getId()))
                    .ifPresent(i -> i.getComments().add(commentMapper.toCommentDtoResponse(comment))));
        }
    }

    private ItemDtoResponse setBookingsDates(Item item, Long userId) {
        ItemDtoResponse itemDto = itemMapper.toItemDtoResponse(item);

        if (Objects.equals(itemDto.getOwner().getId(), userId)) {
            List<BookingDtoInform> bookings = bookingRepository.findBookingsByItemId(itemDto.getId(),
                            LocalDateTime.now()).stream()
                    .limit(2)
                    .sorted(Comparator.comparing(Booking::getStartDate))
                    .map(bookingMapper::toBookingDtoInform)
                    .collect(Collectors.toList());

            itemDto.setLastBooking(!bookings.isEmpty() ? bookings.get(0) : null);
            itemDto.setNextBooking(bookings.size() == 2 ? bookings.get(1) : null);
        }

        return itemDto;
    }

    private List<ItemDtoResponse> setBookingsDates(List<Item> items) {
       List<ItemDtoResponse> itemsDto = items.stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());

        itemsDto.forEach(itemDto -> {
            List<BookingDtoInform> bookings = bookingRepository.findBookingsByItemId(itemDto.getId(),
                            LocalDateTime.now()).stream()
                    .limit(2)
                    .sorted(Comparator.comparing(Booking::getStartDate))
                    .map(bookingMapper::toBookingDtoInform)
                    .collect(Collectors.toList());
            itemDto.setLastBooking(!bookings.isEmpty() ? bookings.get(0) : null);
            itemDto.setNextBooking(bookings.size() == 2 ? bookings.get(1) : null);
        });

        return itemsDto;
    }
}