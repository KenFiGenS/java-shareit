package ru.practicum.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.user.userDto.UserDto;

import java.util.List;

@Component
public class UserClient {
    private static final String API_PREFIX = "/users";
    private RestTemplate restTemplate;


    public UserClient(@Value("${shareIt-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();

    }

    protected UserDto get(long userId) {
            return restTemplate.getForEntity("/" + userId, UserDto.class).getBody();
    }

    protected UserDto post(UserDto body) {
        return restTemplate.postForEntity("", body, UserDto.class).getBody();
    }

    protected UserDto patch(long userId, UserDto body) {
        return restTemplate.patchForObject("/" + userId, body, UserDto.class);
    }

    protected void delete( long userId) {
        restTemplate.delete("/" + userId);
    }

    public List<UserDto> getAll() {
        return restTemplate.getForObject("", List.class);
    }
}
