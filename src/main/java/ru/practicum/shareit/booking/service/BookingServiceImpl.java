package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingsAccessException;
import ru.practicum.shareit.exceptions.IncorrectStateException;
import ru.practicum.shareit.exceptions.ItemIsUnavailableException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDtoResponse addBooking(BookingDtoCreate bookingDtoCreate, Long userId) {
        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart())) {
            throw new IncorrectStateException("end_date can not be in present or greater then start_date");
        }

        this.checkIsTheSameUser(bookingDtoCreate.getItemId(), userId);

        if (!itemService.getById(bookingDtoCreate.getItemId(), userId).getAvailable()) {
            throw new ItemIsUnavailableException(String.format("Item with id %d is unavailable to book",
                    bookingDtoCreate.getItemId()));
        }

        Booking booking = mapper.toBookingEntity(bookingDtoCreate);
        booking.setBooker(userMapper.toUserEntity(userService.getUserById(userId)))
                .setStatus(BookStatus.WAITING);

        return mapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDtoResponse addApprove(String approve, Long userId, Long bookingId) {
        Booking booking = bookingRepository.getReferenceById(bookingId);

        if ((approve.equals("true") && booking.getStatus().equals(BookStatus.APPROVED))
                || (approve.equals("false") && booking.getStatus().equals(BookStatus.REJECTED))) {
            throw new IncorrectStateException(String.format("Booking is already %s",
                    Objects.requireNonNull(BookStatus.fromLabel(approve)).name()));
        }

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
                throw new IncorrectStateException("Approve state can be only 'true' or 'false'");
        }

        return mapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsByState(Long bookerId, String state) {
        userService.getUserById(bookerId);

        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        if (!STATE_PARAMS.contains(state)) {
            throw new IncorrectStateException(String.format("Unknown state: %s", state));
        }

        switch (valueOfLabel(state)) {
            case ALL:
                bookings = bookingRepository.findAll(Sort.by(Sort.Order.desc("startDate"))).stream()
                        .filter(v -> v.getBooker().getId().equals(bookerId))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(bookerId, time);
                break;
            case PAST:
                bookings = bookingRepository.findBookingByBookerIdAndEndDateIsBefore(bookerId, time,
                        Sort.by(Sort.Order.desc("startDate")));
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingByBookerIdAndEndDateIsAfter(bookerId, time,
                        Sort.by(Sort.Order.desc("startDate")));
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingByBookerIdAndStatusRejectedOrWaiting(bookerId,
                        BookStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingByBookerIdAndStatusRejectedOrWaiting(bookerId,
                        BookStatus.WAITING);
                break;
            default:
                break;
        }

        return bookings.stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsByStateForOwner(Long userId, String state) {
        userService.getUserById(userId);

        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        if (!STATE_PARAMS.contains(state)) {
            throw new IncorrectStateException(String.format("Unknown state: %s", state));
        }

        switch (valueOfLabel(state)) {
            case ALL:
                bookings = bookingRepository.findBookingsByItemOwnerId(userId);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByItemOwnerIdInPast(userId, time);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItemOwnerIdInFuture(userId, time);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByItemOwner(userId, time);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByItemOwnerWithWaitingOrRejectedStatus(userId,
                        BookStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByItemOwnerWithWaitingOrRejectedStatus(userId,
                        BookStatus.REJECTED);
        }

        return bookings.stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = Optional.of(bookingRepository.getReferenceById(bookingId))
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Booking with id %d not found", bookingId)));
        this.checkIsOwnerOrBooker(bookingId, booking.getItem().getId(), userId);

        return mapper.toBookingDtoResponse(booking);
    }

    private void checkIsTheSameUser(Long itemId, Long userId) {
        if (Objects.equals(itemService.getById(itemId, userId).getOwner().getId(), userId)) {
            throw new BookingsAccessException("You can not book your own item");
        }
    }

    private void checkIsOwner(Long itemId, Long ownerId) {
        if (!Objects.equals(itemService.getById(itemId, ownerId).getOwner().getId(), ownerId)) {
            throw new BookingsAccessException("You have no access to edit this booking");
        }
    }

    private void checkIsOwnerOrBooker(Long bookingId, Long itemId, Long userId) {
        if (!Objects.equals(bookingRepository.getReferenceById(bookingId).getBooker().getId(), userId)
                && !Objects.equals(itemService.getById(itemId, userId).getOwner().getId(), userId)) {
            throw new BookingsAccessException("You have no access to this booking");
        }
    }
}