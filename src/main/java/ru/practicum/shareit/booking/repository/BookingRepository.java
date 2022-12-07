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

    List<Booking> findBookingByBookerIdAndEndDateIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    @Query("SELECT bo from Booking bo where bo.booker.id = :bookerId and bo.startDate <= :start \n" +
            "and bo.endDate >= :start order by bo.startDate desc")
    List<Booking> findCurrentBookings(Long bookerId, LocalDateTime start);

    @Query("SELECT bo from Booking bo where bo.status = :state \n" +
            "and bo.booker.id = :bookerId \n" +
            " order by bo.startDate desc")
    List<Booking> findBookingByBookerIdAndStatusRejectedOrWaiting(Long bookerId, BookStatus state);

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
            "and bo.endDate >= :start and bo.startDate <= :start \n" +
            "order by bo.startDate desc")
    List<Booking> findCurrentBookingsByItemOwner(Long ownerId, LocalDateTime start);

    @Query("SELECT bo from Booking bo inner join Item i on bo.item.id = i.id \n" +
            "where i.owner.id = :ownerId \n" +
            "and bo.status = :status \n" +
            " order by bo.startDate desc")
    List<Booking> findBookingsByItemOwnerWithWaitingOrRejectedStatus(Long ownerId, BookStatus status);

    @Query("select bo from Booking bo \n" +
            "where bo.item.id = :itemId \n " +
            "and (bo.endDate <= \n " +
            "(select max(bo1.endDate) from Booking bo1 where bo1.item.id = :itemId and bo1.endDate <= :start) or \n" +
            "bo.startDate >= \n" +
            "(select min(bo1.startDate) from Booking bo1 where bo1.item.id = :itemId and bo1.startDate >= :start))")
    List<Booking> findBookingsByItemId(Long itemId, LocalDateTime start);

    @Query("select bo from Booking bo \n" +
            "where bo.item.id = :itemId \n" +
            "and bo.booker.id = :userId \n" +
            "and bo.endDate <= :time")
    List<Booking> findBookingsByUserAndItemId(Long itemId, Long userId, LocalDateTime time);
}
