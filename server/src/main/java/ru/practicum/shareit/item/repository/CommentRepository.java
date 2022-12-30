package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>  {
    Set<Comment> findCommentsByItemId(Long itemId);

    @Query("select co from Comment co " +
            "where co.item.id in (:itemsId)")
    Set<Comment> findCommentsByItemId(Set<Long> itemsId);
}
