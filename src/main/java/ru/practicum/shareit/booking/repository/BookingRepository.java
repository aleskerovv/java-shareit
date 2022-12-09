package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerIdAndEndDateIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findBookingByBookerIdAndStartDateIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query("SELECT bo from Booking bo " +
            "where bo.booker.id = :bookerId " +
            "and bo.startDate <= :start " +
            "and bo.endDate >= :start order by bo.startDate desc")
    List<Booking> findCurrentBookings(Long bookerId, LocalDateTime start);

    @Query("SELECT bo from Booking bo " +
            "where bo.status = :state " +
            "and bo.booker.id = :bookerId " +
            " order by bo.startDate desc")
    List<Booking> findBookingByBookerIdByStatus(Long bookerId, BookStatus state);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerId(Long ownerId);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "and bo.endDate < :end " +
            "order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerIdInPast(Long ownerId, LocalDateTime end);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "and bo.startDate > :end " +
            "order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerIdInFuture(Long ownerId, LocalDateTime end);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "and bo.endDate >= :start " +
            "and bo.startDate <= :start " +
            "order by bo.startDate desc")
    List<Booking> findCurrentBookingsByItemOwner(Long ownerId, LocalDateTime start);

    @Query("SELECT bo from Booking bo " +
            "where bo.item.owner.id = :ownerId " +
            "and bo.status = :status " +
            " order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerByStatus(Long ownerId, BookStatus status);

    List<Booking> findBookingsByItemId(Long itemId);

    @Query("select bo from Booking bo " +
            "where bo.item.id = :itemId " +
            "and bo.booker.id = :userId " +
            "and bo.endDate <= :time")
    List<Booking> findBookingsByUserAndItemId(Long itemId, Long userId, LocalDateTime time);
}
