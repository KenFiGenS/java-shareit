package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toCommentForCreated(CommentDto commentDto, Item item, User user) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                LocalDateTime.now()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
