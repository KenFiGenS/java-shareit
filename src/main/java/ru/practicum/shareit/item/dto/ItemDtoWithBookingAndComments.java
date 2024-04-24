package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoItemById;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBookingAndComments {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    BookingDtoItemById lastBooking;
    BookingDtoItemById nextBooking;
    List<CommentDto> comments;
}
