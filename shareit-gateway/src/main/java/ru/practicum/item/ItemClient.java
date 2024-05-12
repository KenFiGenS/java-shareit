package ru.practicum.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.exception.DataNotFound;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemDtoWithBookingAndComments;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static ru.practicum.constans.Constants.REQUEST_HEADER_NAME;

@Component
public class ItemClient {
    private static final String API_PREFIX = "/items";
    private RestTemplate restTemplate;


    public ItemClient(@Value("${shareIt-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ItemDto create(long userId, ItemDto item) {
        HttpEntity<ItemDto> entityReq = getHttpEntity(userId, item);
        try {
            return restTemplate.exchange("/", HttpMethod.POST, entityReq, ItemDto.class).getBody();
        } catch (HttpStatusCodeException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    public ItemDto updateItem(long userId, long id, ItemDto item) {
        HttpEntity<ItemDto> entityReq = getHttpEntity(userId, item);
        return restTemplate.exchange("/" + id, HttpMethod.PATCH, entityReq, ItemDto.class).getBody();
    }

    public ItemDtoWithBookingAndComments getItemById(long userId, long itemId) {
        HttpEntity<ItemDto> entityReq = getHttpEntity(userId, null);
        try {
            return restTemplate.exchange("/" + itemId, HttpMethod.GET, entityReq, ItemDtoWithBookingAndComments.class).getBody();
        } catch (HttpStatusCodeException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    public List<ItemDtoWithBookingAndComments> getAllItemByOwner(long userId) {
        HttpEntity<ItemDto> entityReq = getHttpEntity(userId, null);
        return restTemplate.exchange("", HttpMethod.GET, entityReq, List.class).getBody();
    }

    public List<ItemDto> search(String text) {
        HttpEntity<ItemDto> entityReq = getHttpEntity(0, null);
        return restTemplate.exchange("/search?text=" + text, HttpMethod.GET, entityReq, List.class).getBody();
    }

    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        HttpEntity<CommentDto> entityReq = getHttpEntityForCommentDto(userId, commentDto);
        try {
            return restTemplate.exchange("/" + itemId + "/comment", HttpMethod.POST, entityReq, CommentDto.class).getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getMessage().contains("400")) throw new IllegalArgumentException(e.getMessage());
            throw new DataNotFound(e.getMessage());
        }
    }

    private HttpEntity<ItemDto> getHttpEntity(long userId, ItemDto itemDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId == 0) {
            return new HttpEntity<>(headers);
        }
        headers.set(REQUEST_HEADER_NAME, String.valueOf(userId));
        if (itemDto == null) {
            return new HttpEntity<>(headers);
        }
        return new HttpEntity<>(itemDto, headers);
    }

    private HttpEntity<CommentDto> getHttpEntityForCommentDto(long userId, CommentDto commentDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(REQUEST_HEADER_NAME, String.valueOf(userId));
        return new HttpEntity<>(commentDto, headers);
    }
}
