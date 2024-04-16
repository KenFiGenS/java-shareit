package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@Entity
@Table(name = "ITEM_REQUESTS")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_REQUEST_ID")
    private long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User requester;
    LocalDateTime created;
}
