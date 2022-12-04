package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemIsUnavailableException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingState.STATE_PARAMS;
import static ru.practicum.shareit.booking.BookingState.valueOfLabel;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;

    @Override
    public BookingDtoResponse addBooking(BookingDtoCreate bookingDtoCreate, Long userId) {
        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart())) {
            throw new UnknownStateException("asdasd");
        }

        if (bookingDtoCreate.getStart().isBefore(LocalDateTime.now())) {
            throw new UnknownStateException("asdasd 2");
        }

        this.checkIsTheSameUser(bookingDtoCreate.getItemId(), userId);

        if (!itemService.getById(bookingDtoCreate.getItemId()).getAvailable()) {
            throw new ItemIsUnavailableException(String.format("Item with id %d is unavailable to book",
                    bookingDtoCreate.getItemId()));
        }

        Booking booking = mapper.toBookingEntity(bookingDtoCreate);
        booking.setBooker(userMapper.toUserEntity(userService.getUserById(userId)))
                .setStatus(BookStatus.WAITING);

        return mapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse addApprove(String approve, Long userId, Long bookingId) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Long itemId = booking.getItem().getId();

        this.checkIsOwner(itemId, userId);

        switch (approve) {
            case "true":
                booking.setStatus(BookStatus.APPROVED);
                break;
            case "false":
                booking.setStatus(BookStatus.REJECTED);
                break;
            default:
                break;
        }

        return mapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDtoResponse> getBookingsByState(Long bookerId, String state) {
        bookingRepository.getReferenceById(bookerId);

        List<Booking> bookings = new ArrayList<>();

        if (!STATE_PARAMS.contains(state)) {
            throw new UnknownStateException(String.format("Unknown state: '%s'", state));
        }

        switch (valueOfLabel(state)) {
            case ALL:
                bookings = bookingRepository.findAll(Sort.by(Sort.Order.desc("startDate"))).stream()
                        .filter(v -> v.getBooker().getId().equals(bookerId))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(bookerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findBookingByBookerIdAndEndDateIsBefore(bookerId, LocalDateTime.now()
                                , Sort.by(Sort.Order.desc("startDate")));
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingByBookerIdAndEndDateIsAfter(bookerId, LocalDateTime.now()
                                , Sort.by(Sort.Order.desc("startDate")));
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingByBookerIdAndStatusRejected(bookerId);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingByBookerIdAndStatusWaiting(bookerId);
                break;
            default:
                break;
        }

        return bookings.stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getBookingsByStateForOwner(Long userId, String state) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        if (!STATE_PARAMS.contains(state)) {
            throw new UnknownStateException(String.format("Unknown state: '%s'", state));
        }

        switch (valueOfLabel(state)) {
            case ALL:
                bookings = bookingRepository.findBookingsByItemOwnerId(userId);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByItemOwnerIdInPast(userId, endTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItemOwnerIdInFuture(userId, endTime);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByItemOwner(userId, startTime, endTime);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByItemOwnerWithWaitingStatus(userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByItemOwnerWithRejectedStatus(userId);
        }

        return bookings.stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> findBookingsByItemId(Long itemId, LocalDateTime start) {
        return bookingRepository.findBookingsByItemId(itemId, start).stream()
                .limit(2)
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDtoResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        this.check(bookingId, booking.getItem().getId(), userId);

        return mapper.toBookingDtoResponse(booking);
    }

    private void checkIsTheSameUser(Long itemId, Long userId) {
        if (Objects.equals(itemService.getById(itemId).getOwner().getId(), userId)) {
            throw new NoAccessException("You can not book your own item");
        }
    }

    private void checkIsOwner(Long itemId, Long ownerId) {
        if (!Objects.equals(itemService.getById(itemId).getOwner().getId(), ownerId)) {
            throw new NoAccessException("You have no access to edit this booking");
        }
    }

    private void check(Long bookingId, Long itemId, Long userId) {
        if (!Objects.equals(bookingRepository.getReferenceById(bookingId).getBooker().getId(), userId)
                && !Objects.equals(itemService.getById(itemId).getOwner().getId(), userId)) {
            throw new NoAccessException("You have no access to this booking");
        }
    }
}