package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
