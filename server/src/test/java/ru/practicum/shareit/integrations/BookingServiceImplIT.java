package ru.practicum.shareit.integrations;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.IncorrectStateException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIT {
    private final BookingService bookingService;

    @Test
    void set_approveToBookingByOwner_andBookingStatusIsAPPROVED() {
        BookingDtoResponse response = bookingService.addApprove("true", 2L, 3L);

        assertThat(response.getStatus(), equalTo(BookStatus.APPROVED));
    }

    @Test
    void set_approveToBookingByOwner_andBookingStatusIsREJECTED() {
        BookingDtoResponse response = bookingService.addApprove("false", 2L, 3L);

        assertThat(response.getStatus(), equalTo(BookStatus.REJECTED));
    }

    @Test
    void getAllBookingsByStateByUser() {
        List<BookingDtoResponse> response = bookingService.getBookingsByState(1L, "ALL", 0, 3);

        assertThat(response.size(), equalTo(1));
        assertThat(response.get(0).getId(), equalTo(3L));
    }

    @Test
    void getCurrentBookingsByUser() {
        List<BookingDtoResponse> response = bookingService.getBookingsByState(1L, "CURRENT", 0, 3);

        assertThat(response.size(), equalTo(1));
        assertThat(response.get(0).getId(), equalTo(3L));
    }

    @Test
    void getPastBookingsByUser() {
        List<BookingDtoResponse> response = bookingService.getBookingsByState(3L, "PAST", 0, 3);

        assertThat(response.size(), equalTo(1));
        assertThat(response.get(0).getId(), equalTo(1L));
    }

    @Test
    void getFutureBookingsByUser() {
        List<BookingDtoResponse> response = bookingService.getBookingsByState(2L, "FUTURE", 0, 3);

        assertThat(response.size(), equalTo(1));
        assertThat(response.get(0).getId(), equalTo(4L));
    }

    @Test
    void getRejectedBookingsByUser() {
        List<BookingDtoResponse> response = bookingService.getBookingsByState(2L,
                "REJECTED", 0, 3);

        assertThat(response.size(), equalTo(0));
    }

    @Test
    void getWaitingBookingsByUser() {
        List<BookingDtoResponse> response = bookingService.getBookingsByState(2L,
                "WAITING", 0, 3);

        assertThat(response.size(), equalTo(2));
    }

    @Test
    void whenUnknownState_thenThrowsException() {
        final IncorrectStateException ex = assertThrows(
                IncorrectStateException.class,
                () -> bookingService.getBookingsByState(1L, "ASD", 0, 2)
        );

        assertThat("Unknown state: ASD", equalTo(ex.getMessage()));
    }

    @Test
    void getBookingsByOwner() {
        List<BookingDtoResponse> responses = bookingService.getBookingsByStateForOwner(1L,
                "ALL", 0, 10);

        assertThat(responses.size(), equalTo(4));
    }

    @Test
    void getCurrentBookingsByOwner() {
        List<BookingDtoResponse> responses = bookingService.getBookingsByStateForOwner(1L,
                "CURRENT", 0, 10);

        assertThat(responses.size(), equalTo(0));
    }

    @Test
    void getPastBookingsByOwner() {
        List<BookingDtoResponse> responses = bookingService.getBookingsByStateForOwner(1L,
                "PAST", 0, 10);

        assertThat(responses.size(), equalTo(2));
    }

    @Test
    void getFutureBookingsByOwner() {
        List<BookingDtoResponse> responses = bookingService.getBookingsByStateForOwner(1L,
                "FUTURE", 0, 10);

        assertThat(responses.size(), equalTo(2));
    }

    @Test
    void getWaitingBookingsByOwner() {
        List<BookingDtoResponse> responses = bookingService.getBookingsByStateForOwner(1L,
                "WAITING", 0, 10);

        assertThat(responses.size(), equalTo(3));
    }

    @Test
    void getRejectedBookingsByOwner() {
        List<BookingDtoResponse> responses = bookingService.getBookingsByStateForOwner(1L,
                "REJECTED", 0, 10);

        assertThat(responses.size(), equalTo(0));
    }
}