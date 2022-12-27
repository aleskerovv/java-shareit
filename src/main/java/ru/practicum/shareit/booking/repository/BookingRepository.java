package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerIdAndEndDateIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStartDateIsAfter(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Query("SELECT bo from Booking bo " +
            "where bo.booker.id = :bookerId " +
            "and bo.startDate <= :start " +
            "and bo.endDate >= :start")
    List<Booking> findCurrentBookings(Long bookerId, LocalDateTime start, Pageable pageable);

    @Query("SELECT bo from Booking bo " +
            "where bo.status = :state " +
            "and bo.booker.id = :bookerId ")
    List<Booking> findBookingByBookerIdByStatus(Long bookerId, BookStatus state, Pageable pageable);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId ")
    List<Booking> findBookingsByItemOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "and bo.endDate < :end ")
    List<Booking> findBookingsByItemOwnerIdInPast(Long ownerId, LocalDateTime end, Pageable pageable);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "and bo.startDate > :end ")
    List<Booking> findBookingsByItemOwnerIdInFuture(Long ownerId, LocalDateTime end, Pageable pageable);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "and bo.endDate >= :start " +
            "and bo.startDate <= :start ")
    List<Booking> findCurrentBookingsByItemOwner(Long ownerId, LocalDateTime start, Pageable pageable);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "and bo.status = :status ")
    List<Booking> findBookingsByItemOwnerByStatus(Long ownerId, BookStatus status, Pageable pageable);

    List<Booking> findBookingsByItemId(Long itemId);

    @Query("select bo from Booking bo " +
            "where bo.item.id = :itemId " +
            "and bo.booker.id = :userId " +
            "and bo.endDate <= :time")
    List<Booking> findBookingsByUserAndItemId(Long itemId, Long userId, LocalDateTime time);
}
