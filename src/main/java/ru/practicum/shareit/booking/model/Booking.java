package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Booking booking = (Booking) o;
        return id != null && Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

