package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userDto.UserDto;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    void patch() {
        UserDto userDtoCreate = new UserDto(0, "email@mail.ru", "NameUser");
        UserDto userDtoForPatch = new UserDto(0, "updateEmail@mail.ru", "UpdateName");

        long id = userService.create(userDtoCreate).getId();

        UserDto userDtoAfterPatch = userService.patch(id, userDtoForPatch);
        assertEquals(1, userDtoAfterPatch.getId());
        assertEquals("updateEmail@mail.ru", userDtoAfterPatch.getEmail());
        assertEquals("UpdateName", userDtoAfterPatch.getName());
    }
}