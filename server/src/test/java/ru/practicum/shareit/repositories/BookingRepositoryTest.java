package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.utils.PageConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts = {"file:src/test/resources/test-schema.sql", "file:src/test/resources/test-data.sql"})
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    private static final Pageable PAGEABLE = PageConverter.toPageRequest(0, 10)
            .withSort(Sort.Direction.DESC, "startDate");

    @Test
    void test_allBookings_byUser() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(2L, PAGEABLE);

        assertThat(bookings.size(), equalTo(2));
    }

    @Test
    void findBookingByBookerIdAndEndDateIsBefore_andReturns_2() {
        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndEndDateIsBefore(
                3L, LocalDateTime.now(), PAGEABLE);

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void findBookingByBookerIdAndStartDateIsAfter_andReturns_1() {
        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndEndDateIsBefore(
                3L, LocalDateTime.now(), PAGEABLE);

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void findAllByBookerId_andReturns_2() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(
                3L, PAGEABLE);

        assertThat(bookings.size(), equalTo(2));
    }

    @Test
    void findCurrentBookings_andReturns_1() {
        List<Booking> bookings = bookingRepository.findCurrentBookings(1L,
                LocalDateTime.now(), PAGEABLE);

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void findBookingByBookerIdByStatusWAITING_andReturns_2() {
        List<Booking> bookings = bookingRepository.findBookingByBookerIdByStatus(2L,
                BookStatus.WAITING, PAGEABLE);

        assertThat(bookings.size(), equalTo(2));
    }

    @Test
    void findBookingByBookerIdByStatusREJECTED_andReturns_0() {
        List<Booking> bookings = bookingRepository.findBookingByBookerIdByStatus(2L,
                BookStatus.REJECTED, PAGEABLE);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findBookingsByItemOwnerId_andReturns_4() {
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerId(
                1L, PAGEABLE);

        assertThat(bookings.size(), equalTo(4));
    }

    @Test
    void findBookingsByItemOwnerIdInPast_andReturns_2() {
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerIdInPast(
                1L, LocalDateTime.now(), PAGEABLE);

        assertThat(bookings.size(), equalTo(2));
    }

    @Test
    void findBookingsByItemOwnerIdInFuture_andReturns_2() {
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerIdInFuture(
                1L, LocalDateTime.now(), PAGEABLE);

        assertThat(bookings.size(), equalTo(2));
    }

    @Test
    void findCurrentBookingsByItemOwner_andReturns_2() {
        List<Booking> bookings = bookingRepository.findCurrentBookingsByItemOwner(
                2L, LocalDateTime.now(), PAGEABLE);

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void findBookingsByItemOwnerByStatusWAITING_andReturns_3() {
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerByStatus(
                1L, BookStatus.WAITING, PAGEABLE);

        assertThat(bookings.size(), equalTo(3));
    }

    @Test
    void findBookingsByItemOwnerByStatusREJECTED_andReturns_1() {
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerByStatus(
                1L, BookStatus.REJECTED, PAGEABLE);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findBookingsByItemId_andReturns_2() {
        List<Booking> bookings = bookingRepository.findBookingsByItemId(
                1L);

        assertThat(bookings.size(), equalTo(2));
    }

    @Test
    void findBookingsByUserAndItemId_andReturns_1() {
        List<Booking> bookings = bookingRepository.findBookingsByUserAndItemId(
                1L, 3L, LocalDateTime.now());

        assertThat(bookings.size(), equalTo(1));
    }
}
