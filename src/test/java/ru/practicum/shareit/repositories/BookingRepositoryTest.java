package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.utils.PageConverter;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private static final Pageable PAGEABLE = PageConverter.toPageRequest(0, 10).
            withSort(Sort.Direction.DESC, "startDate");

    @Test
    void test_allBookings_byUser() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(1L, PAGEABLE);

        assertThat(bookings.size(), equalTo(1));
    }
}
