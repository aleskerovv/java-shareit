package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir from ItemRequest ir " +
            "where ir.requester.id <> :userId")
    Page<ItemRequest> findAllRequests(Long userId, Pageable pageable);

    @Query("SELECT ir FROM ItemRequest ir " +
            "where ir.requester.id = :userId " +
            "order by ir.created desc")
    List<ItemRequest> findAllByRequesterId(Long userId);
}
