package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;


    @Test
    @DirtiesContext
    void getRequestsByUserTest() {
        User booker = UserMapper.toUser(userService.create(new UserDto(0, "email@mail.ru", "NameUser")));
        UserDto owner1 = userService.create(new UserDto(0, "email2@mail.ru", "NameUser2"));
        UserDto owner2 = userService.create(new UserDto(0, "email3@mail.ru", "NameUser3"));
        ItemRequestDto itemRequest1 = itemRequestService.createItemRequest(1L, new ItemRequestDto(
                0,
                "Хотел бы воспользоваться щёткой для обуви",
                booker,
                null,
                null
        ));

        ItemRequestDto itemRequest2 = itemRequestService.createItemRequest(1L, new ItemRequestDto(
                0,
                "Хотел бы воспользоваться зубной щёткой",
                booker,
                null,
                null
        ));

        List<ItemRequestDto> itemRequestDtoFromRepository = itemRequestService.getRequestsByUser(1);
        assertEquals(2, itemRequestDtoFromRepository.size());
        assertThrows(ResponseStatusException.class,
                () -> itemRequestService.getRequestsByUser(4));

    }

    @Test
    @DirtiesContext
    void getRequestsAll() {
        User booker = UserMapper.toUser(userService.create(new UserDto(0, "email@mail.ru", "NameUser")));
        UserDto owner1 = userService.create(new UserDto(0, "email2@mail.ru", "NameUser2"));
        UserDto owner2 = userService.create(new UserDto(0, "email3@mail.ru", "NameUser3"));
        ItemRequestDto itemRequest1 = itemRequestService.createItemRequest(1L, new ItemRequestDto(
                0,
                "Хотел бы воспользоваться щёткой для обуви",
                booker,
                null,
                null
        ));

        ItemRequestDto itemRequest2 = itemRequestService.createItemRequest(1L, new ItemRequestDto(
                0,
                "Хотел бы воспользоваться зубной щёткой",
                booker,
                null,
                null
        ));

        List<ItemRequestDto> itemRequestDtoFromRepository = itemRequestService.getRequestsAll(2, 0, 5);
        assertEquals(2, itemRequestDtoFromRepository.size());
        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(1, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(1, 0, -1));
        itemRequestDtoFromRepository = itemRequestService.getRequestsAll(1, 1, -1);
        assertEquals(0, itemRequestDtoFromRepository.size());
        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(1, -1, 5));
    }
}