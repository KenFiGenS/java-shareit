package ru.practicum.request;

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
import ru.practicum.booking.dto.BookingDtoCreate;
import ru.practicum.exception.DataNotFound;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

import static ru.practicum.constans.Constants.REQUEST_HEADER_NAME;

@Component
public class RequestClient {
    private static final String API_PREFIX = "/requests";
    private RestTemplate restTemplate;

    public RequestClient(@Value("${shareIt-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ItemRequestDto createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        HttpEntity<ItemRequestDto> httpEntity = getHttpEntity(userId, itemRequestDto);
        try {
            return restTemplate.exchange("", HttpMethod.POST, httpEntity, ItemRequestDto.class).getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getMessage().contains("400")) throw new IllegalArgumentException(e.getMessage());
            throw new DataNotFound(e.getMessage());
        }

    }

    public ItemRequestDto getRequestById(long userId, long requestId) {
        HttpEntity<ItemRequestDto> httpEntity = getHttpEntity(userId, null);
        try {
            return restTemplate.exchange("/" + requestId, HttpMethod.GET, httpEntity, ItemRequestDto.class).getBody();
        } catch (HttpStatusCodeException e) {
            throw new DataNotFound(e.getMessage());
        }
    }

    public List<ItemRequestDto> getRequestsByUser(long userId) {
        HttpEntity<ItemRequestDto> httpEntity = getHttpEntity(userId, null);
        try {
            return restTemplate.exchange("", HttpMethod.GET, httpEntity, List.class).getBody();
        } catch (HttpStatusCodeException e) {
            throw new DataNotFound(e.getMessage());
        }
    }

    private HttpEntity<ItemRequestDto> getHttpEntity(long userId, ItemRequestDto itemRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId == 0) {
            return new HttpEntity<>(headers);
        }
        headers.set(REQUEST_HEADER_NAME, String.valueOf(userId));
        if (itemRequestDto == null) {
            return new HttpEntity<>(headers);
        }
        return new HttpEntity<>(itemRequestDto, headers);
    }


    public List<ItemRequestDto> getRequestsAll(long userId, int from, int size) {
        HttpEntity<ItemRequestDto> httpEntity = getHttpEntity(userId, null);
        try {
            return restTemplate.exchange("/all?from=" + from + "&size=" + size, HttpMethod.GET, httpEntity, List.class).getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getMessage().contains("400")) throw new IllegalArgumentException(e.getMessage());
            throw new DataNotFound(e.getMessage());
        }
    }
}
