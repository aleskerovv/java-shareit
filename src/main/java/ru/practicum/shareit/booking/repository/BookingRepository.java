package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerIdAndEndDateIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findBookingByBookerIdAndEndDateIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    @Query("SELECT bo from Booking bo where bo.startDate >= :start \n" +
            "and bo.endDate <= :end and bo.id = :bookerId order by bo.startDate desc")
    List<Booking> findCurrentBookings(Long bookerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT bo from Booking bo where bo.status = 'REJECTED' \n" +
            "and bo.booker.id = :bookerId \n" +
            " order by bo.startDate desc")
    List<Booking> findBookingByBookerIdAndStatusRejected(Long bookerId);

    @Query("SELECT bo from Booking bo where bo.status = 'WAITING' \n" +
            "and bo.booker.id = :bookerId \n" +
            " order by bo.startDate desc")
    List<Booking> findBookingByBookerIdAndStatusWaiting(Long bookerId);

    @Query("SELECT bo from Booking bo inner join Item i on bo.item.id = i.id \n " +
            "where i.owner.id = :ownerId order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerId(Long ownerId);

    @Query("SELECT bo from Booking bo inner join Item i on bo.item.id = i.id \n " +
            "where i.owner.id = :ownerId \n" +
            "and bo.endDate < :end \n" +
            "order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerIdInPast(Long ownerId, LocalDateTime end);

    @Query("SELECT bo from Booking bo inner join Item i on bo.item.id = i.id \n " +
            "where i.owner.id = :ownerId \n" +
            "and bo.endDate > :end \n" +
            "order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerIdInFuture(Long ownerId, LocalDateTime end);

    @Query("SELECT bo from Booking bo inner join Item i on bo.item.id = i.id \n " +
            "where i.owner.id = :ownerId \n" +
            "and bo.endDate <= :end and bo.startDate >= :start \n" +
            "order by bo.startDate desc")
    List<Booking> findCurrentBookingsByItemOwner(Long ownerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT bo from Booking bo inner join Item i on bo.item.id = i.id \n" +
            "where i.owner.id = :ownerId \n" +
            "and bo.status = 'WAITING' \n" +
            " order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerWithWaitingStatus(Long ownerId);

    @Query("SELECT bo from Booking bo inner join Item i on bo.item.id = i.id \n" +
            "where i.owner.id = :ownerId \n" +
            "and bo.status = 'REJECTED' \n" +
            " order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerWithRejectedStatus(Long ownerId);

    @Query("select bo from Booking bo \n" +
            "where bo.item.id = :itemId \n " +
            "and (bo.endDate <= :start or \n" +
            "bo.startDate >= :start)")
    List<Booking> findBookingsByItemId(Long itemId, LocalDateTime start);
}
