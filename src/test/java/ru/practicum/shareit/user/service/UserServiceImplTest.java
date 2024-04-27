package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    UserService userService;
    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createTest() {
        UserDto userDtoBeforeSave = new UserDto(0, "email@mail.ru", "NameUser");
        User userAfterSave = new User(1, "email@mail.ru", "NameUser");

        when(userRepository.save(any())).thenReturn(userAfterSave);

        UserDto userDtoResult = userService.create(userDtoBeforeSave);
        Assertions.assertEquals(1, userDtoResult.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void patchTestThrowDataNotFoundException() {
        assertThrows(
                DataNotFound.class,
                () -> userService.patch(4, new UserDto(0, "email@mail.ru", "NameUser")));
    }

    @Test
    void patchTest() {
        User userInRepository = new User(1, "email@mail.ru", "NameUser");
        UserDto userDtoForPatch = new UserDto(0, "updateEmail@mail.ru", "UpdateName");
        User userAfterPatch = new User(1, "updateEmail@mail.ru", "UpdateName");

        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(userInRepository);
        when(userRepository.save(any())).thenReturn(userAfterPatch);

        UserDto userDtoResult = userService.patch(1, userDtoForPatch);
        Assertions.assertEquals(1, userDtoResult.getId());
        Assertions.assertEquals("updateEmail@mail.ru", userDtoResult.getEmail());
        Assertions.assertEquals("UpdateName", userDtoResult.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void patchEmailTest() {
        User userInRepository = new User(1, "email@mail.ru", "NameUser");
        UserDto userDtoForPatch = new UserDto(0, "updateEmail@mail.ru", null);
        User userAfterPatch = new User(1, "updateEmail@mail.ru", "NameUser");

        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(userInRepository);
        when(userRepository.save(any())).thenReturn(userAfterPatch);

        UserDto userDtoResult = userService.patch(1, userDtoForPatch);
        Assertions.assertEquals(1, userDtoResult.getId());
        Assertions.assertEquals("updateEmail@mail.ru", userDtoResult.getEmail());
        Assertions.assertEquals("NameUser", userDtoResult.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void patchNameTest() {
        User userInRepository = new User(1, "email@mail.ru", "NameUser");
        UserDto userDtoForPatch = new UserDto(0, null, "UpdateName");
        User userAfterPatch = new User(1, "email@mail.ru", "UpdateName");

        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(userInRepository);
        when(userRepository.save(any())).thenReturn(userAfterPatch);

        UserDto userDtoResult = userService.patch(1, userDtoForPatch);
        Assertions.assertEquals(1, userDtoResult.getId());
        Assertions.assertEquals("email@mail.ru", userDtoResult.getEmail());
        Assertions.assertEquals("UpdateName", userDtoResult.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserByIdTestThrowDataNotFoundException() {
        assertThrows(
                DataNotFound.class,
                () -> userService.getUserById(4));
    }

    @Test
    void getUserByIdTest() {
        User userInRepository = new User(1, "email@mail.ru", "NameUser");

        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(userInRepository);

        UserDto userDtoResult = userService.getUserById(1);
        Assertions.assertEquals(1, userDtoResult.getId());
        Assertions.assertEquals("email@mail.ru", userDtoResult.getEmail());
        Assertions.assertEquals("NameUser", userDtoResult.getName());
    }

    @Test
    void getAllUsersTestIsEmpty() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> allUsers = userService.getAllUsers();
        assertEquals(0, allUsers.size());
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(
                        new User(1,"123@mail.ru", "Boris"),
                        new User(2, "456@mail,ru", "Morris")
                ));

        List<UserDto> allUsers = userService.getAllUsers();
        assertEquals(2, allUsers.size());
    }

    @Test
    void removeUserTestThrowDataNotFoundException() {
        assertThrows(
                DataNotFound.class,
                () -> userService.removeUser(1));
    }

    @Test
    void removeUserTest() {
        User userInRepository = new User(1, "email@mail.ru", "NameUser");

        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(userInRepository);

        userService.removeUser(1);
        verify(userRepository).deleteById(anyLong());
    }

}