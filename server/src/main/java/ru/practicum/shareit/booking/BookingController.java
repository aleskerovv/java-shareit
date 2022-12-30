package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoResponse createBooking(@Valid @RequestBody BookingDtoCreate bookingDtoCreate,
                                            @RequestHeader("X-Sharer-User-Id") Long id) {
        return bookingService.addBooking(bookingDtoCreate, id);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoResponse addApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "approved") String approved,
                                         @PathVariable("bookingId") Long bookingId) {
        return bookingService.addApprove(approved, userId, bookingId);
    }

    @GetMapping("{bookingId}")
    public BookingDtoResponse getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(value = "state", required = false,
                                                                 defaultValue = "ALL") String state,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "0") int from,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "10") int size) {
        return bookingService.getBookingsByState(userId, state, from, size);
    }

    @GetMapping("owner")
    public List<BookingDtoResponse> getAllBookingsByStateForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                  @RequestParam(value = "state",
                                                                          required = false,
                                                                          defaultValue = "ALL") String state,
                                                                  @RequestParam(required = false,
                                                                          defaultValue = "0") int from,
                                                                  @RequestParam(required = false,
                                                                          defaultValue = "10") int size) {
        return bookingService.getBookingsByStateForOwner(userId, state, from, size);
    }
}
