package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
        Item item = Optional.of(itemRepository.getReferenceById(itemId))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Item with id %d not found", itemId)));
        ItemDtoResponse itemDto = this.setBookingsDates(item, userId);
        this.setCommentsToItem(itemDto);
        return itemDto;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemDtoResponse create(ItemDto itemDto, Long userId) {
        Item item = itemMapper.toItemEntity(itemDto);
        item.setOwner(userMapper.toUserEntity(userService.getUserById(userId)));
        return itemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemDtoResponse update(ItemDto itemDto, Long itemId, Long userId) {
        log.info("updating item with id {} by user {}", itemId, userId);
        this.checkItemsOwner(itemId, userId);

        Item item = Optional.of(itemRepository.getReferenceById(itemId)).orElseThrow(() ->
                new EntityNotFoundException(String.format("Item with id %d not found", itemId)));

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription()
                : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable()
                : item.getAvailable());
        log.info("item with id {} updated", itemId);
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CommentDtoResponse addComment(CommentDto commentDto, Long userId, Long itemId) {
        if (bookingRepository.findBookingsByUserAndItemId(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new IncorrectStateException("You are not have any ended booking for this item");
        }
        Comment comment = commentMapper.toCommentEntity(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(itemRepository.getItemById(itemId));
        comment.setAuthor(userMapper.toUserEntity(userService.getUserById(userId)));

        log.info("user with id {} added a comment to item with id {}", userId, itemId);
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

        Set<Comment> comments = new HashSet<>(commentRepository.findCommentsByItemId(itemsDto.keySet()));

        if (!itemsDto.isEmpty()) {
            comments.forEach(comment -> Optional.ofNullable(itemsDto.get(comment.getItem().getId()))
                    .ifPresent(i -> i.getComments().add(commentMapper.toCommentDtoResponse(comment))));
        }
    }

    private ItemDtoResponse setBookingsDates(Item item, Long userId) {
        ItemDtoResponse itemDto = itemMapper.toItemDtoResponse(item);

        if (Objects.equals(itemDto.getOwner().getId(), userId)) {
//            findBookings(itemDto);

            List<Booking> bookings = bookingRepository.findBookingsByItemId(itemDto.getId());

            Optional<Booking> lastBooking = bookings.stream()
                    .filter(v -> v.getEndDate().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getStartDate));

            Optional<Booking> nextBooking = bookings.stream()
                    .filter(v -> v.getStartDate().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStartDate));

            itemDto.setLastBooking(lastBooking.map(bookingMapper::toBookingDtoInform).orElse(null));
            itemDto.setNextBooking(nextBooking.map(bookingMapper::toBookingDtoInform).orElse(null));
        }

        return itemDto;
    }

    private List<ItemDtoResponse> setBookingsDates(List<Item> itemsList) {
        List<ItemDtoResponse> items = itemsList.stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());

        Set<Booking> bookings = new HashSet<>(bookingRepository.findAll());

        if (!items.isEmpty()) {
            items.forEach(item -> {
                        Optional<Booking> lastBooking = bookings.stream()
                                .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                                .filter(booking -> booking.getEndDate().isBefore(LocalDateTime.now()))
                                .max(Comparator.comparing(Booking::getEndDate));

                        Optional<Booking> nextBooking = bookings.stream()
                                .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                                .filter(booking -> booking.getStartDate().isAfter(LocalDateTime.now()))
                                .min(Comparator.comparing(Booking::getStartDate));

                        item.setLastBooking(lastBooking.map(bookingMapper::toBookingDtoInform).orElse(null));
                        item.setNextBooking(nextBooking.map(bookingMapper::toBookingDtoInform).orElse(null));
                    });
        }

        return items;
    }
}