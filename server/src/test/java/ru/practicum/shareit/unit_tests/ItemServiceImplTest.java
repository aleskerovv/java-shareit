package ru.practicum.shareit.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.utils.PageConverter;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private ItemServiceImpl itemService;
    private final ItemMapper mapper = new ItemMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final CommentMapper commentMapper = new CommentMapperImpl();
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;

    private final Item item = new Item();
    private final User user = new User();
    private final User user2 = new User();
    private final Comment comment = new Comment();
    private final Booking booking = new Booking();
    private final Item item2 = new Item();
    private final Booking booking2 = new Booking();

    @BeforeEach
    void init() {
        this.itemService = new ItemServiceImpl(commentRepository, commentMapper,
                bookingRepository, bookingMapper, itemRepository, mapper, userService, userMapper);
        ReflectionTestUtils.setField(mapper, "userMapper", userMapper);

        user.setEmail("j_doe@gmail.com")
                .setName("John Doe")
                .setId(1L);
        user2.setName("Elvis Presley")
                .setEmail("elvis@foo.com")
                .setId(2L);
        item.setOwner(user)
                .setAvailable(true)
                .setDescription("New item for booking")
                .setName("New item")
                .setItemRequest(new ItemRequest())
                .setId(1L);
        item2.setOwner(user)
                .setAvailable(true)
                .setDescription("New where booking in the past")
                .setName("Another new")
                .setItemRequest(new ItemRequest())
                .setId(2L);
        comment.setItem(item2)
                .setCreated(LocalDateTime.now())
                .setText("Text :)")
                .setAuthor(user2)
                .setId(1L);
        booking.setStatus(BookStatus.WAITING)
                .setItem(item)
                .setBooker(user2)
                .setStartDate(LocalDateTime.now())
                .setEndDate(LocalDateTime.now().plusDays(15))
                .setId(1L);
        booking2.setStatus(BookStatus.APPROVED)
                .setItem(item2)
                .setBooker(user2)
                .setStartDate(LocalDateTime.now().minusDays(15))
                .setEndDate(LocalDateTime.now().minusDays(2))
                .setId(2L);
    }

    @Test
    void findItemsByOwnerId() {
        Pageable pageable = PageConverter.toPageRequest(0, 1);

        Mockito.when(itemRepository.getItemsByOwnerId(1L, pageable))
                .thenReturn(List.of(item));
        Mockito.when(bookingRepository.findAll())
                .thenReturn(List.of(booking));
        Mockito.when(commentRepository.findCommentsByItemId(Set.of(item.getId())))
                .thenReturn(Set.of(comment));

        List<ItemDtoResponse> items = itemService.getItemsByOwnerId(1L, 0, 1);

        assertEquals(1, items.size());
        assertEquals(items.get(0).getName(), item.getName());
        Mockito.verify(bookingRepository, times(1))
                .findAll();
        Mockito.verify(commentRepository,
                        times(1))
                .findCommentsByItemId(Mockito.anySet());
    }

    @Test
    void getItemByIdByOwner() {
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item);
        Mockito.when(bookingRepository.findBookingsByItemId(1L))
                .thenReturn(List.of(booking));
        Mockito.when(commentRepository.findCommentsByItemId(1L))
                .thenReturn(Set.of(comment));

        ItemDtoResponse itemDtoResponse = itemService.getById(1L, 1L);
        assertEquals(itemDtoResponse.getName(), item.getName());
        Mockito.verify(bookingRepository, times(1))
                .findBookingsByItemId(1L);
        Mockito.verify(commentRepository,
                        times(1))
                .findCommentsByItemId(1L);
    }

    @Test
    void getItemByIdByOtherUser() {
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item);
        Mockito.when(commentRepository.findCommentsByItemId(1L))
                .thenReturn(Set.of(comment));

        ItemDtoResponse itemDtoResponse = itemService.getById(1L, 2L);
        assertEquals(itemDtoResponse.getName(), item.getName());
        Mockito.verify(commentRepository,
                        times(1))
                .findCommentsByItemId(1L);
    }

    @Test
    void test_addComment() {
        Mockito.when(commentRepository.save(comment))
                .thenReturn(comment.setId(1L));

        Mockito.doReturn(List.of(booking2))
                .when(bookingRepository)
                .findBookingsByUserAndItemId(anyLong(), anyLong(), isA(LocalDateTime.class));

        CommentDtoResponse commentDto = itemService.addComment(
                commentMapper.toCommentDto(comment), 2L, 2L
        );

        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        Mockito.verify(bookingRepository, times(1))
                .findBookingsByUserAndItemId(anyLong(), anyLong(), isA(LocalDateTime.class));
    }

    @Test
    void createNewItem() {
        Mockito.when(itemRepository.save(item))
                .thenReturn(item.setId(1L));

        ItemDtoResponse itemDto = itemService.create(mapper.toItemDto(item), 1L);

        assertEquals(itemDto.getName(), item.getName());
        Mockito.verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void updateItemByOwner() {
        item.setName("Updated name");
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item);

        Mockito.when(itemRepository.save(item))
                .thenReturn(item.setId(1L));

        ItemDtoResponse itemDto = itemService.update(mapper.toItemDto(item), 1L, 1L);

        assertEquals("Updated name", itemDto.getName());

        Mockito.verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void updateItemByAnotherUser_andThrowsException() {
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item);

        final NoAccessException exception = assertThrows(
                NoAccessException.class,
                () -> itemService.update(mapper.toItemDto(item), 1L, 2L)
        );

        assertEquals("You have no access to edit this item", exception.getMessage());
    }

    @Test
    void findItemsByEmptyParams() {
        List<ItemDtoResponse> emptyList = itemService.findByParams("", 0, 3);
        assertEquals(0, emptyList.size());

        Mockito.verify(itemRepository, times(0))
                .getItemsByParams(anyString(), any(Pageable.class));
    }

    @Test
    void findItemsByParams() {
        Pageable pageable = PageConverter.toPageRequest(0, 3);
        Mockito.when(itemRepository.getItemsByParams("item", pageable))
                .thenReturn(List.of(item));

        List<ItemDtoResponse> dtoList = itemService.findByParams("item", 0, 3);
        assertEquals(1, dtoList.size());
        assertEquals(item.getName(), dtoList.get(0).getName());

        Mockito.verify(itemRepository, times(1))
                .getItemsByParams("item", pageable);
    }
}