package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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
