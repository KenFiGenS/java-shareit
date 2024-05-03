package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.exception.NotFoundBookingStatusException;
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

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(userService.getUserById(anyLong())).thenThrow(new DataNotFound("Пользователь не найден"));

        assertThrows(DataNotFound.class,
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

        when(userService.getUserById(anyLong())).thenReturn(booker);
        when(itemRequestRepository.save(any())).thenReturn(itemRequestAfterSave);

        ItemRequestDto itemRequestDtoFromService = itemRequestService.createItemRequest(1L, itemRequestDtoForCreate);
        assertEquals(itemRequestDtoResult, itemRequestDtoFromService);
    }

    @Test
    void getRequestByIdTestThrowResponseStatusException() {
        assertThrows(ResponseStatusException.class,
                () -> itemRequestService.getRequestById(3L, 1));
    }

    @Test
    void getRequestByIdTestThrowDataNotFoundException() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1, "123@mail.ru", "Boris"),
                new User(2, "456@mail,ru", "Morris")
        ));
        when(itemRequestRepository.getReferenceById(anyLong())).thenReturn(null);
        assertThrows(DataNotFound.class,
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

        when(userRepository.findAll()).thenReturn(List.of(
                new User(1, "123@mail.ru", "Boris"),
                new User(2, "456@mail,ru", "Morris")
        ));
        when(itemRequestRepository.getReferenceById(anyLong())).thenReturn(itemRequestAfterSave);
        when(itemRepository.findItemByItemRequestId(anyLong())).thenReturn(null);

        ItemRequestDto itemRequestDtoFromService = itemRequestService.getRequestById(1, 1);
        assertEquals(itemRequestDtoResult, itemRequestDtoFromService);
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

        when(userRepository.findAll()).thenReturn(List.of(UserMapper.toUser(booker), owner));
        when(itemRequestRepository.getReferenceById(anyLong())).thenReturn(itemRequestAfterSave);
        when(itemRepository.findItemByItemRequestId(anyLong())).thenReturn(itemFromRepository);

        ItemRequestDto itemRequestDtoFromService = itemRequestService.getRequestById(1, 1);
        assertEquals(itemRequestDtoResult, itemRequestDtoFromService);
    }

    @Test
    void getRequestsByUserTestThrowResponseStatusException() {
        assertThrows(ResponseStatusException.class,
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

        when(userRepository.findAll()).thenReturn(List.of(UserMapper.toUser(booker), owner));
        when(itemRequestRepository.findByRequesterId(anyLong())).thenReturn(itemRequests);
        when(itemRepository.findAll()).thenReturn(itemFromRepository);

        List<ItemRequestDto> itemRequestDtoFromService = itemRequestService.getRequestsByUser(1);
        assertEquals(itemRequestDtoResult, itemRequestDtoFromService);
    }

    @Test
    void getRequestsAllTestThrowIllegalArgumentExceptionSizeAndFromIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(3L, 0, 0));
    }

    @Test
    void getRequestsAllTestThrowIllegalArgumentExceptionSizeIsNegativeAndFromIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(3L, 0, -1));
    }

    @Test
    void getRequestsAllTestSizeIsNegative() {
        List<ItemRequestDto> itemRequestDtoFromService = itemRequestService.getRequestsAll(3L, 5, -1);
        assertEquals(0, itemRequestDtoFromService.size());
    }

    @Test
    void getRequestsAllTestFromIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getRequestsAll(3L, -1, 5));
    }
}