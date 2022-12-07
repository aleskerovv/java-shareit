package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse addBooking(BookingDtoCreate bookingDtoCreate, Long userId);

    BookingDtoResponse addApprove(String bool, Long userId, Long bookingId);

    BookingDtoResponse getBookingById(Long bookingId, Long userId);

    List<BookingDtoResponse> getBookingsByState(Long bookerId, String state);

    List<BookingDtoResponse> getBookingsByStateForOwner(Long bookerId, String state);
}
