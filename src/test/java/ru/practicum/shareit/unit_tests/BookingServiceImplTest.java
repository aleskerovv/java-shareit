package ru.practicum.shareit.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingsAccessException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.PageConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingServiceImpl bookingService;
    private final BookingMapper mapper = new BookingMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();
    private final ItemMapper itemMapper = new ItemMapperImpl();
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    User user = new User();
    Item item = new Item();
    Booking booking = new Booking();
    User user2 = new User();
    Booking booking2 = new Booking();
    Item item2 = new Item();
    Booking booking3 = new Booking();
    Booking booking4 = new Booking();
    Booking booking5 = new Booking();
    Booking booking6 = new Booking();
    static final Pageable PAGEABLE = PageConverter.toPageRequest(0, 10)
            .withSort(Sort.Direction.DESC, "startDate");

    @BeforeEach
    void init() {
        this.bookingService = new BookingServiceImpl(bookingRepository, mapper, userService, userMapper, itemService);
        ReflectionTestUtils.setField(itemMapper, "userMapper", userMapper);
        ReflectionTestUtils.setField(mapper, "userMapper", userMapper);
        ReflectionTestUtils.setField(mapper, "itemRepository", itemRepository);
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
                .setEndDate(LocalDateTime.now().minusDays(2));
        booking3.setStatus(BookStatus.WAITING)
                .setItem(item)
                .setBooker(user2)
                .setStartDate(LocalDateTime.now().plusMonths(1))
                .setEndDate(LocalDateTime.now().plusMonths(2));
        booking4.setStatus(BookStatus.APPROVED)
                .setItem(item)
                .setBooker(user2)
                .setStartDate(LocalDateTime.now().plusDays(5))
                .setEndDate(LocalDateTime.now().plusDays(6));
        booking5.setStatus(BookStatus.REJECTED)
                .setItem(item2)
                .setBooker(user2)
                .setStartDate(LocalDateTime.now())
                .setEndDate(LocalDateTime.now().plusDays(1));
        booking6.setStatus(BookStatus.WAITING)
                .setItem(item2)
                .setBooker(user2)
                .setStartDate(LocalDateTime.now().plusDays(22))
                .setEndDate(LocalDateTime.now().plusDays(25));
    }

    @Test
    void addBooking() {
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        Mockito.when(itemRepository.getItemById(anyLong()))
                .thenReturn(item.setId(1L));
        Mockito.doReturn(itemMapper.toItemDtoResponse(item)).when(itemService)
                .getById(1L, 2L);

        BookingDtoResponse bookingDto = bookingService.addBooking(
                mapper.toBookingDtoCreate(booking), 2L);

        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getBooker().getName(),
                booking.getBooker().getName());
    }

    @Test
    void setStatusByOwner() {
        Mockito.doReturn(booking).when(bookingRepository)
                .save(booking);
        Mockito.doReturn(booking).when(bookingRepository)
                .getReferenceById(1L);
        Mockito.doReturn(itemMapper.toItemDtoResponse(item)).when(itemService)
                .getById(1L, 1L);

        BookingDtoResponse response = bookingService.addApprove("true",
                1L, 1L);

        assertEquals(BookStatus.APPROVED,
                response.getStatus());

        response = bookingService.addApprove("false",
                1L, 1L);
        booking.setStatus(BookStatus.REJECTED);
        assertEquals(BookStatus.REJECTED, response.getStatus());
    }

    @Test
    void test_GetCurrentBookingsByUser() {
        booking.setStatus(BookStatus.WAITING);
        Mockito.doReturn(userMapper.toUserDto(user2)).when(userService)
                .getUserById(2L);

        Mockito.doReturn(List.of(booking)).when(bookingRepository)
                .findCurrentBookings(eq(2L), any(LocalDateTime.class),
                        eq(PAGEABLE));

        List<BookingDtoResponse> current = bookingService.getBookingsByState(2L,
                "CURRENT", 0, 10);
        assertEquals(1, current.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findCurrentBookings(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void test_getFutureBookingsByUser() {
        Mockito.doReturn(userMapper.toUserDto(user2)).when(userService)
                .getUserById(2L);

        Mockito.doReturn(List.of(booking3, booking4, booking6)).when(bookingRepository)
                .findBookingByBookerIdAndStartDateIsAfter(eq(2L), any(LocalDateTime.class),
                        eq(PAGEABLE));

        List<BookingDtoResponse> future = bookingService.getBookingsByState(2L,
                "FUTURE", 0, 10);
        assertEquals(3, future.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingByBookerIdAndStartDateIsAfter(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void test_getPastBookingsByUser() {
        Mockito.doReturn(userMapper.toUserDto(user2)).when(userService)
                .getUserById(2L);

        Mockito.doReturn(List.of(booking2)).when(bookingRepository)
                .findBookingByBookerIdAndEndDateIsBefore(eq(2L), any(LocalDateTime.class),
                        eq(PAGEABLE));

        List<BookingDtoResponse> past = bookingService.getBookingsByState(2L,
                "PAST", 0, 10);
        assertEquals(1, past.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingByBookerIdAndEndDateIsBefore(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void test_getAllBookingsByStateByUser() {
        Mockito.doReturn(userMapper.toUserDto(user2)).when(userService)
                .getUserById(2L);

        Mockito.doReturn(List.of(booking, booking2, booking3, booking4,
                        booking5, booking6)).when(bookingRepository)
                .findAllByBookerId(2L, PAGEABLE);

        List<BookingDtoResponse> all = bookingService.getBookingsByState(2L,
                "ALL", 0, 10);
        assertEquals(6, all.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerId(anyLong(), any(Pageable.class));
    }

    @Test
    void test_getRejectedBookingsByUser() {
        Mockito.doReturn(userMapper.toUserDto(user2)).when(userService)
                .getUserById(2L);
        Mockito.doReturn(List.of(booking5)).when(bookingRepository)
                .findBookingByBookerIdByStatus(2L, BookStatus.REJECTED,
                        PAGEABLE);

        List<BookingDtoResponse> rejected = bookingService.getBookingsByState(2L,
                "REJECTED", 0, 10);
        assertEquals(1, rejected.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingByBookerIdByStatus(anyLong(), any(BookStatus.class),
                        any(Pageable.class));
    }

    @Test
    void test_getWaitingBookingsByUser() {
        Mockito.doReturn(userMapper.toUserDto(user2)).when(userService)
                .getUserById(2L);

        Mockito.doReturn(List.of(booking, booking6, booking2)).when(bookingRepository)
                .findBookingByBookerIdByStatus(2L, BookStatus.WAITING,
                        PAGEABLE);

        List<BookingDtoResponse> waiting = bookingService.getBookingsByState(2L,
                "WAITING", 0, 10);
        assertEquals(3, waiting.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingByBookerIdByStatus(anyLong(), any(BookStatus.class),
                        any(Pageable.class));
    }

    @Test
    void test_GetCurrentBookingsByOwner() {
        Mockito.doReturn(userMapper.toUserDto(user)).when(userService)
                .getUserById(1L);


        Mockito.doReturn(List.of(booking)).when(bookingRepository)
                .findCurrentBookingsByItemOwner(eq(1L), any(LocalDateTime.class),
                        eq(PAGEABLE));

        List<BookingDtoResponse> current = bookingService.getBookingsByStateForOwner(1L,
                "CURRENT", 0, 10);
        assertEquals(1, current.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findCurrentBookingsByItemOwner(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void test_getFutureBookingsByOwner() {
        Mockito.doReturn(userMapper.toUserDto(user)).when(userService)
                .getUserById(1L);

        Mockito.doReturn(List.of(booking3, booking4, booking6)).when(bookingRepository)
                .findBookingsByItemOwnerIdInFuture(eq(1L), any(LocalDateTime.class),
                        eq(PAGEABLE));

        List<BookingDtoResponse> future = bookingService.getBookingsByStateForOwner(1L,
                "FUTURE", 0, 10);
        assertEquals(3, future.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingsByItemOwnerIdInFuture(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void test_getPastBookingsByOwner() {
        Mockito.doReturn(userMapper.toUserDto(user)).when(userService)
                .getUserById(1L);

        Mockito.doReturn(List.of(booking2)).when(bookingRepository)
                .findBookingsByItemOwnerIdInPast(eq(1L), any(LocalDateTime.class),
                        eq(PAGEABLE));

        List<BookingDtoResponse> past = bookingService.getBookingsByStateForOwner(1L,
                "PAST", 0, 10);
        assertEquals(1, past.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingsByItemOwnerIdInPast(anyLong(), any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void test_getAllBookingsByStateByOwner() {
        Mockito.doReturn(userMapper.toUserDto(user)).when(userService)
                .getUserById(1L);

        Mockito.doReturn(List.of(booking, booking2, booking3, booking4,
                        booking5, booking6)).when(bookingRepository)
                .findBookingsByItemOwnerId(1L, PAGEABLE);

        List<BookingDtoResponse> all = bookingService.getBookingsByStateForOwner(1L,
                "ALL", 0, 10);
        assertEquals(6, all.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingsByItemOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void test_getRejectedBookingsByOwner() {
        Mockito.doReturn(userMapper.toUserDto(user)).when(userService)
                .getUserById(1L);

        Mockito.doReturn(List.of(booking5)).when(bookingRepository)
                .findBookingsByItemOwnerByStatus(1L, BookStatus.REJECTED,
                        PAGEABLE);

        List<BookingDtoResponse> rejected = bookingService.getBookingsByStateForOwner(1L,
                "REJECTED", 0, 10);
        assertEquals(1, rejected.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingsByItemOwnerByStatus(anyLong(), any(BookStatus.class),
                        any(Pageable.class));
    }

    @Test
    void test_getWaitingBookingsByOwner() {
        Mockito.doReturn(userMapper.toUserDto(user)).when(userService)
                .getUserById(1L);

        Mockito.doReturn(List.of(booking5)).when(bookingRepository)
                .findBookingsByItemOwnerByStatus(1L, BookStatus.WAITING,
                        PAGEABLE);

        List<BookingDtoResponse> rejected = bookingService.getBookingsByStateForOwner(1L,
                "WAITING", 0, 10);
        assertEquals(1, rejected.size());

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingsByItemOwnerByStatus(anyLong(), any(BookStatus.class),
                        any(Pageable.class));
    }

    @Test
    void test_whenOwnerBooksOwnItem_andThrowsException() {
        Mockito.doReturn(itemMapper.toItemDtoResponse(item)).when(itemService)
                .getById(1L, 1L);

        final BookingsAccessException exception = assertThrows(
                BookingsAccessException.class,
                () -> bookingService.addBooking(mapper.toBookingDtoCreate(booking),
                        1L)
        );

        assertEquals("You can not book your own item", exception.getMessage());
    }

    @Test
    void test_whenOtherUserAddsApprove_andThrowsException() {
        Mockito.doReturn(booking).when(bookingRepository)
                .getReferenceById(1L);
        Mockito.doReturn(itemMapper.toItemDtoResponse(item)).when(itemService)
                .getById(1L, 2L);

        final BookingsAccessException ex = assertThrows(
                BookingsAccessException.class,
                () -> bookingService.addApprove("true", 2L, 1L)
        );

        assertEquals("You have no access to edit this booking", ex.getMessage());
    }

    @Test
    void test_getBookingById() {
        Mockito.doReturn(booking).when(bookingRepository)
                .getReferenceById(1L);
        Mockito.doReturn(itemMapper.toItemDtoResponse(item)).when(itemService)
                .getById(1L, 1L);

        BookingDtoResponse bookingDto = bookingService.getBookingById(1L, 1L);

        assertEquals(bookingDto.getBooker().getName(), booking.getBooker().getName());

        Mockito.verify(bookingRepository, Mockito.times(2))
                .getReferenceById(1L);
    }
}