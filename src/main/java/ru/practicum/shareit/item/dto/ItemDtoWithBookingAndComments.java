package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoItemById;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBookingAndComments {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoItemById lastBooking;
    private BookingDtoItemById nextBooking;
    private List<CommentDto> comments;
}
