package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    ItemRequestService itemRequestService;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(
                userService,
                itemRequestRepository,
                itemRepository,
                userRepository
        );
    }


    @Test
    void createItemRequestTestThrowDataNotFoundException() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                0,
                "Хотел бы воспользоваться щёткой для обуви",
                null,
                null,
                null);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenThrow(new DataNotFound("Пользователь не найден"));

        Assertions.assertThrows(DataNotFound.class,
                () -> itemRequestService.createItemRequest(3L, itemRequestDto));
    }

    @Test
    void createItemRequestTest() {
        UserDto booker = new UserDto(1, "email@mail.ru", "NameUser");
        ItemRequestDto itemRequestDtoForCreate = new ItemRequestDto(
                0,
                "Хотел бы воспользоваться щёткой для обуви",
                null,
                null,
                null);
        ItemRequest itemRequestAfterSave = new ItemRequest(
                1,
                "Хотел бы воспользоваться щёткой для обуви",
                UserMapper.toUser(booker),
                LocalDateTime.now()
        );
        ItemRequestDto itemRequestDtoResult = ItemRequestMapper.toItemRequestDto(itemRequestAfterSave, null);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(booker);
        Mockito.when(itemRequestRepository.save(ArgumentMatchers.any())).thenReturn(itemRequestAfterSave);

        ItemRequestDto itemRequestDtoFromService = itemRequestService.createItemRequest(1L, itemRequestDtoForCreate);
        Assertions.assertEquals(itemRequestDtoResult, itemRequestDtoFromService);
    }

    @Test
    void getRequestByIdTestThrowResponseStatusException() {
        Assertions.assertThrows(ResponseStatusException.class,
                () -> itemRequestService.getRequestById(3L, 1));
    }

    @Test
    void getRequestByIdTestThrowDataNotFoundException() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(
                new User(1, "123@mail.ru", "Boris"),
                new User(2, "456@mail,ru", "Morris")
        ));
        Mockito.when(itemRequestRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(null);
        Assertions.assertThrows(DataNotFound.class,
                () -> itemRequestService.getRequestById(1L, 1));
    }

    @Test
    void getRequestByIdTestItemListIsEmpty() {
        UserDto booker = new UserDto(1, "email@mail.ru", "NameUser");
        ItemRequest itemRequestAfterSave = new ItemRequest(
                1,
                "Хотел бы воспользоваться щёткой для обуви",
                UserMapper.toUser(booker),
                LocalDateTime.now()
        );
        ItemRequestDto itemRequestDtoResult = ItemRequestMapper.toItemRequestDto(itemRequestAfterSave, List.of());

        Mockito.when(userRepository.findAll()).thenReturn(List.of(
                new User(1, "123@mail.ru", "Boris"),
                new User(2, "456@mail,ru", "Morris")
        ));
        Mockito.when(itemRequestRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemRequestAfterSave);
        Mockito.when(itemRepository.findItemByItemRequestId(ArgumentMatchers.anyLong())).thenReturn(null);

        ItemRequestDto itemRequestDtoFromService = itemRequestService.getRequestById(1, 1);
        Assertions.assertEquals(itemRequestDtoResult, itemRequestDtoFromService);
    }

    @Test
    void getRequestByIdTestWithItemList() {
        UserDto booker = new UserDto(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "456@mail,ru", "Morris");
        ItemRequest itemRequestAfterSave = new ItemRequest(
                1,
                "Хотел бы воспользоваться щёткой для обуви",
                UserMapper.toUser(booker),
                LocalDateTime.now()
        );
        List<Item> itemFromRepository = List.of(new Item(1, "Hammer", "Big hammer", true, owner, null));
        List<ItemDto> itemsByRequest = itemFromRepository.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDto itemRequestDtoResult = ItemRequestMapper.toItemRequestDto(itemRequestAfterSave, itemsByRequest);

        Mockito.when(userRepository.findAll()).thenReturn(List.of(UserMapper.toUser(booker), owner));
        Mockito.when(itemRequestRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemRequestAfterSave);
        Mockito.when(itemRepository.findItemByItemRequestId(ArgumentMatchers.anyLong())).thenReturn(itemFromRepository);

        ItemRequestDto itemRequestDtoFromService = itemRequestService.getRequestById(1, 1);
        Assertions.assertEquals(itemRequestDtoResult, itemRequestDtoFromService);
    }

    @Test
    void getRequestsByUserTestThrowResponseStatusException() {
        Assertions.assertThrows(ResponseStatusException.class,
                () -> itemRequestService.getRequestsByUser(3L));
    }

    @Test
    void getRequestsByUserTest() {
        UserDto booker = new UserDto(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "456@mail,ru", "Morris");
        ItemRequest itemRequestAfterSave = new ItemRequest(
                1,
                "Хотел бы воспользоваться щёткой для обуви",
                UserMapper.toUser(booker),
                LocalDateTime.now()
        );
        List<Item> itemFromRepository = List.of(new Item(1, "Hammer", "Big hammer", true, owner, itemRequestAfterSave));
        List<ItemDto> itemsByRequest = itemFromRepository.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        List<ItemRequest> itemRequests = List.of((itemRequestAfterSave));
        List<ItemRequestDto> itemRequestDtoResult = List.of(ItemRequestMapper.toItemRequestDto(itemRequestAfterSave, itemsByRequest));

        Mockito.when(userRepository.findAll()).thenReturn(List.of(UserMapper.toUser(booker), owner));
        Mockito.when(itemRequestRepository.findByRequesterId(ArgumentMatchers.anyLong())).thenReturn(itemRequests);
        Mockito.when(itemRepository.findAll()).thenReturn(itemFromRepository);

        List<ItemRequestDto> itemRequestDtoFromService = itemRequestService.getRequestsByUser(1);
        Assertions.assertEquals(itemRequestDtoResult, itemRequestDtoFromService);
    }

    @Test
    void getRequestsAllTestThrowIllegalArgumentExceptionSizeAndFromIsZero() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(3L, 0, 0));
    }

    @Test
    void getRequestsAllTestThrowIllegalArgumentExceptionSizeIsNegativeAndFromIsZero() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(3L, 0, -1));
    }

    @Test
    void getRequestsAllTestSizeIsNegative() {
        List<ItemRequestDto> itemRequestDtoFromService = itemRequestService.getRequestsAll(3L, 5, -1);
        Assertions.assertEquals(0, itemRequestDtoFromService.size());
    }

    @Test
    void getRequestsAllTestFromIsNegative() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(3L, -1, 5));
    }
}