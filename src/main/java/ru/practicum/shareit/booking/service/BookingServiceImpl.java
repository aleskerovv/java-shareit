package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingsAccessException;
import ru.practicum.shareit.exceptions.IncorrectStateException;
import ru.practicum.shareit.exceptions.ItemIsUnavailableException;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.enums.BookingState.valueOfLabel;
import static ru.practicum.shareit.utils.PageConverter.toPageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDtoResponse addBooking(BookingDtoCreate bookingDtoCreate, Long userId) {
        log.info("adding new booking from user with id {}", userId);
        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart())) {
            throw new IncorrectStateException("end_date can not be in present or greater then start_date");
        }

        this.checkUserIsOwner(bookingDtoCreate.getItemId(), userId);

        if (!itemService.getById(bookingDtoCreate.getItemId(), userId).getAvailable()) {
            throw new ItemIsUnavailableException(String.format("Item with id %d is unavailable to book",
                    bookingDtoCreate.getItemId()));
        }

        Booking booking = mapper.toBookingEntity(bookingDtoCreate);
        booking.setBooker(userMapper.toUserEntity(userService.getUserById(userId)))
                .setStatus(BookStatus.WAITING);

        BookingDtoResponse response = mapper.toBookingDtoResponse(bookingRepository.save(booking));
        log.info("added new booking: {}", response);

        return response;
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
        BookingDtoResponse response = mapper.toBookingDtoResponse(bookingRepository.save(booking));
        log.info("user with id {} approved booking with id {}", userId, bookingId);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getBookingsByState(Long bookerId, String state, int from, int size) {
        this.verifyPagination(from, size);
        Pageable pageable = toPageRequest(from, size).withSort(Sort.Direction.DESC, "startDate");
        userService.getUserById(bookerId);

        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        if (!BookingState.getStateParams().contains(state)) {
            throw new IncorrectStateException(String.format("Unknown state: %s", state));
        }

        switch (valueOfLabel(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(bookerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(bookerId, time, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findBookingByBookerIdAndEndDateIsBefore(bookerId, time,
                        pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingByBookerIdAndStartDateIsAfter(bookerId, time,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingByBookerIdByStatus(bookerId,
                        BookStatus.REJECTED, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingByBookerIdByStatus(bookerId,
                        BookStatus.WAITING, pageable);
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
    public List<BookingDtoResponse> getBookingsByStateForOwner(Long userId, String state, int from, int size) {
        this.verifyPagination(from, size);
        Pageable pageable = toPageRequest(from, size).withSort(Sort.Direction.DESC, "startDate");
        userService.getUserById(userId);

        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        if (!BookingState.getStateParams().contains(state)) {
            throw new IncorrectStateException(String.format("Unknown state: %s", state));
        }

        switch (valueOfLabel(state)) {
            case ALL:
                bookings = bookingRepository.findBookingsByItemOwnerId(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByItemOwnerIdInPast(userId, time, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItemOwnerIdInFuture(userId, time, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByItemOwner(userId, time, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByItemOwnerByStatus(userId,
                        BookStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByItemOwnerByStatus(userId,
                        BookStatus.REJECTED, pageable);
        }

        return bookings.stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingById(Long bookingId, Long userId) {
        try {
            Booking booking = bookingRepository.getReferenceById(bookingId);

            this.checkIsOwnerOrBooker(bookingId, booking.getItem().getId(), userId);

            return mapper.toBookingDtoResponse(booking);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(String.format("Booking with id %d not found", bookingId));
        }
    }

    private void checkUserIsOwner(Long itemId, Long userId) {
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

    private void verifyPagination(int from, int size) {
        if (from < 0) throw new PaginationException("Page index must not be less than zero");
        if (size < 1) throw new PaginationException("Page size must not be less than one");
    }
}