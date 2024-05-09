package ru.practicum.booking;

import lombok.SneakyThrows;
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
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoCreate;
import ru.practicum.exception.DataNotFound;
import ru.practicum.exception.NotFoundBookingStatusException;

import java.util.List;

import static ru.practicum.constans.Constants.REQUEST_HEADER_NAME;

@Component
public class BookingClient {
    private static final String API_PREFIX = "/bookings";
    private RestTemplate restTemplate;


    public BookingClient(@Value("${shareIt-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public BookingDto createBooking(long userId, BookingDtoCreate bookingDto) {
        HttpEntity<BookingDtoCreate> httpEntity = getHttpEntity(userId, bookingDto);
        try {
            return restTemplate.exchange("", HttpMethod.POST, httpEntity, BookingDto.class).getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getMessage().contains("400")) throw new IllegalArgumentException(e.getMessage());
            throw new DataNotFound(e.getMessage());
        }
    }

    public BookingDto confirmationBooking(long userId, long bookingId, boolean approved) {
        HttpEntity<BookingDtoCreate> httpEntity = getHttpEntity(userId, null);
        try {
            return restTemplate.exchange("/" + bookingId + "?approved=" + approved, HttpMethod.PATCH, httpEntity, BookingDto.class).getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getMessage().contains("400")) throw new IllegalArgumentException(e.getMessage());
            throw new DataNotFound(e.getMessage());
        }
    }

    public BookingDto getBookingById(long userId, long bookingId) {
        HttpEntity<BookingDtoCreate> httpEntity = getHttpEntity(userId, null);
        try {
            return restTemplate.exchange("/" + bookingId, HttpMethod.GET, httpEntity, BookingDto.class).getBody();
        } catch (HttpStatusCodeException e) {
            throw new DataNotFound(e.getMessage());
        }
    }

    @SneakyThrows
    public List<BookingDto> getAllBookingsByBooker(long userId, String state, int from, int size) {
        String url = "?from=" + from + "&size=" + size + "&state=" + state;
        HttpEntity<BookingDtoCreate> httpEntity = getHttpEntity(userId, null);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, httpEntity, List.class).getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getMessage().contains("UNSUPPORTED"))
                throw new NotFoundBookingStatusException("Unknown state: " + state);
            throw new DataNotFound(e.getMessage());
        }

    }

    @SneakyThrows
    public List<BookingDto> getAllBookingsByOwner(long userId, String state, int from, int size) {
        String url = "/owner?from=" + from + "&size=" + size + "&state=" + state;
        HttpEntity<BookingDtoCreate> httpEntity = getHttpEntity(userId, null);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, httpEntity, List.class).getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getMessage().contains("UNSUPPORTED"))
                throw new NotFoundBookingStatusException("Unknown state: " + state);
            throw new DataNotFound(e.getMessage());
        }
    }

    private HttpEntity<BookingDtoCreate> getHttpEntity(long userId, BookingDtoCreate bookingDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId == 0) {
            return new HttpEntity<>(headers);
        }
        headers.set(REQUEST_HEADER_NAME, String.valueOf(userId));
        if (bookingDto == null) {
            return new HttpEntity<>(headers);
        }
        return new HttpEntity<>(bookingDto, headers);
    }
}
