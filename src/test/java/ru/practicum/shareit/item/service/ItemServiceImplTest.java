package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository,
                userService,
                bookingRepository,
                commentRepository
        );
    }

    @Test
    void createItemTest() {
        UserDto userDtoFromUserService = new UserDto(1, "email@mail.ru", "NameUser");
        ItemDto itemDtoBeforeSave = new ItemDto(0, "Hammer", "Big hammer", true);
        Item itemAfterSave = new Item(1, "Hammer", "Big hammer", true, UserMapper.toUser(userDtoFromUserService));

        when(userService.getUserById(anyLong())).thenReturn(userDtoFromUserService);
        when(itemRepository.save(any())).thenReturn(itemAfterSave);

        ItemDto itemDtoAfterSave = itemService.createItem(1, itemDtoBeforeSave);
        assertEquals(1, itemDtoAfterSave.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItemTestThrowDataNotFoundException() {
        assertThrows(
                DataNotFound.class,
                () -> itemService.updateItem(1, 1, new ItemDto(0, "Hammer", "Big hammer", true))
        );
    }

    @Test
    void updateItemTest() {
        User owner = new User(1, "email@mail.ru", "NameUser");
        Item itemFromRepository = new Item(1, "Hammer", "Big hammer", true, owner);
        ItemDto itemDtoForUpdate = new ItemDto(0, "NewHammer", "Very Big hammer", true);
        Item itemAfterUpdate = new Item(1, "NewHammer", "Very Big hammer", true, owner);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemFromRepository);
        when(itemRepository.save(any())).thenReturn(itemAfterUpdate);

        ItemDto itemDtoUpdateResult = itemService.updateItem(1,1, itemDtoForUpdate);
        assertEquals(1, itemDtoUpdateResult.getId());
        assertEquals("NewHammer", itemAfterUpdate.getName());
        assertEquals("Very Big hammer", itemAfterUpdate.getDescription());
        assertEquals(true, itemAfterUpdate.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItemNameTest() {
        User owner = new User(1, "email@mail.ru", "NameUser");
        Item itemFromRepository = new Item(1, "Hammer", "Big hammer", true, owner);
        ItemDto itemDtoForUpdate = new ItemDto(0, "NewHammer", null, null);
        Item itemAfterUpdate = new Item(1, "NewHammer", "Big hammer", true, owner);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemFromRepository);
        when(itemRepository.save(any())).thenReturn(itemAfterUpdate);

        ItemDto itemDtoUpdateResult = itemService.updateItem(1,1, itemDtoForUpdate);
        assertEquals(1, itemDtoUpdateResult.getId());
        assertEquals("NewHammer", itemAfterUpdate.getName());
        assertEquals("Big hammer", itemAfterUpdate.getDescription());
        assertEquals(true, itemAfterUpdate.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItemDescriptionTest() {
        User owner = new User(1, "email@mail.ru", "NameUser");
        Item itemFromRepository = new Item(1, "Hammer", "Big hammer", true, owner);
        ItemDto itemDtoForUpdate = new ItemDto(0, null, "Very Big hammer", null);
        Item itemAfterUpdate = new Item(1, "Hammer", "Very Big hammer", true, owner);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemFromRepository);
        when(itemRepository.save(any())).thenReturn(itemAfterUpdate);

        ItemDto itemDtoUpdateResult = itemService.updateItem(1,1, itemDtoForUpdate);
        assertEquals(1, itemDtoUpdateResult.getId());
        assertEquals("Hammer", itemAfterUpdate.getName());
        assertEquals("Very Big hammer", itemAfterUpdate.getDescription());
        assertEquals(true, itemAfterUpdate.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItemAvailableTest() {
        User owner = new User(1, "email@mail.ru", "NameUser");
        Item itemFromRepository = new Item(1, "Hammer", "Big hammer", true, owner);
        ItemDto itemDtoForUpdate = new ItemDto(0, null, null, false);
        Item itemAfterUpdate = new Item(1, "Hammer", "Big hammer", false, owner);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemFromRepository);
        when(itemRepository.save(any())).thenReturn(itemAfterUpdate);

        ItemDto itemDtoUpdateResult = itemService.updateItem(1,1, itemDtoForUpdate);
        assertEquals(1, itemDtoUpdateResult.getId());
        assertEquals("Hammer", itemAfterUpdate.getName());
        assertEquals("Big hammer", itemAfterUpdate.getDescription());
        assertEquals(false, itemAfterUpdate.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getItemByIdTest() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        List<Comment> comments = List.of(
                new Comment(1, "Comment text",itemForBooking, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking, booker, LocalDateTime.now().minusDays(1))
        );

        when(bookingRepository.findByItemId(anyLong())).thenReturn(bookingsFromRepository);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForBooking);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(comments);

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        assertEquals(1, itemDtoWithBookingAndComments.getId());
        assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        assertEquals(2, itemDtoWithBookingAndComments.getLastBooking().getId());
        assertEquals(1, itemDtoWithBookingAndComments.getNextBooking().getId());
        assertEquals(2, itemDtoWithBookingAndComments.getComments().size());
    }

    @Test
    void getItemByIdTestNextBookingIsEmpty() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        List<Comment> comments = List.of(
                new Comment(1, "Comment text",itemForBooking, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking, booker, LocalDateTime.now().minusDays(1))
        );

        when(bookingRepository.findByItemId(anyLong())).thenReturn(bookingsFromRepository);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForBooking);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(comments);

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        assertEquals(1, itemDtoWithBookingAndComments.getId());
        assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        assertEquals(2, itemDtoWithBookingAndComments.getLastBooking().getId());
        assertNull(itemDtoWithBookingAndComments.getNextBooking());
        assertEquals(2, itemDtoWithBookingAndComments.getComments().size());
    }

    @Test
    void getItemByIdTestLastBookingIsEmpty() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED)
        );
        List<Comment> comments = List.of(
                new Comment(1, "Comment text",itemForBooking, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking, booker, LocalDateTime.now().minusDays(1))
        );

        when(bookingRepository.findByItemId(anyLong())).thenReturn(bookingsFromRepository);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForBooking);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(comments);

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        assertEquals(1, itemDtoWithBookingAndComments.getId());
        assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        assertNull(itemDtoWithBookingAndComments.getLastBooking());
        assertEquals(1, itemDtoWithBookingAndComments.getNextBooking().getId());
        assertEquals(2, itemDtoWithBookingAndComments.getComments().size());
    }

    @Test
    void getItemByIdTestBookingIsEmptyWithComments() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);

        List<Comment> comments = List.of(
                new Comment(1, "Comment text",itemForBooking, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking, booker, LocalDateTime.now().minusDays(1))
        );

        when(bookingRepository.findByItemId(anyLong())).thenReturn(List.of());
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForBooking);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(comments);

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        assertEquals(1, itemDtoWithBookingAndComments.getId());
        assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        assertNull(itemDtoWithBookingAndComments.getLastBooking());
        assertNull(itemDtoWithBookingAndComments.getNextBooking());
        assertEquals(2, itemDtoWithBookingAndComments.getComments().size());
    }

    @Test
    void getItemByIdTestBookingWithCommentsIsEmpty() {
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);

        when(bookingRepository.findByItemId(anyLong())).thenReturn(List.of());
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForBooking);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        assertEquals(1, itemDtoWithBookingAndComments.getId());
        assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        assertNull(itemDtoWithBookingAndComments.getLastBooking());
        assertNull(itemDtoWithBookingAndComments.getNextBooking());
        assertEquals(List.of(), itemDtoWithBookingAndComments.getComments());
    }

    @Test
    void getAllItemByOwner() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner1 = new User(2, "email2@mail.ru", "NameUser2");
        User owner2 = new User(3, "email3@mail.ru", "NameUser3");
        Item itemForBooking1 = new Item(1, "Hammer", "Big hammer", true, owner1);
        Item itemForBooking2 = new Item(2, "Glasses", "Big Glasses", true, owner1);
        Item itemForBooking3 = new Item(3, "Camera", "Professional camera", true, owner2);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking1, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking1, booker, BookingStatus.APPROVED)
        );
        List<Comment> comments = List.of(
                new Comment(1, "Comment text",itemForBooking1, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking1, booker, LocalDateTime.now().minusDays(1))
        );
        List<Item> itemsFromRepository = List.of(itemForBooking1, itemForBooking2);

        when(itemRepository.findItemByOwnerId(anyLong())).thenReturn(itemsFromRepository);
        when(bookingRepository.findByItemId(1L)).thenReturn(bookingsFromRepository);
        when(bookingRepository.findByItemId(2L)).thenReturn(List.of());
        when(itemRepository.getReferenceById(1L)).thenReturn(itemForBooking1);
        when(itemRepository.getReferenceById(2L)).thenReturn(itemForBooking2);
        when(commentRepository.findAllByItemId(1L)).thenReturn(comments);
        when(commentRepository.findAllByItemId(2L)).thenReturn(List.of());

        List<ItemDtoWithBookingAndComments> itemDtoResult = itemService.getAllItemByOwner(1);
        assertEquals(2, itemDtoResult.size());
    }

    @Test
    void searchTest() {
        User user = new User(0, "NameUser@mail.ru", "NameUser");
        Item item1 = new Item(1, "Hammer", "Big hammer", true, user);
        Item item2 = new Item(2, "Knife", "Small", true, user);
        Item item3 = new Item(3, "Sword", "Sword of knight", true, user);
        List<Item> itemSearchResult = List.of(item2, item3);

        when(itemRepository.searchByNameAndDescription(anyString())).thenReturn(itemSearchResult);

        List<ItemDto> searchResult = itemService.search("Kni");
        assertEquals(2, searchResult.size());
        assertEquals(2, searchResult.get(0).getId());
        assertEquals(3, searchResult.get(1).getId());
    }

    @Test
    void createCommentTest() {
        UserDto userDto = new UserDto(1, "email@mail.ru", "NameUser");
        User user = new User(1, "email@mail.ru", "NameUser");
        User owner1 = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForComment = new Item(1, "Hammer", "Big hammer", true, owner1);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForComment, user, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForComment, user, BookingStatus.APPROVED)
        );
        CommentDto commentDto = new CommentDto(0,  "Comment text", null, null);
        Comment comment = new Comment(1, "Comment text", itemForComment, user, LocalDateTime.now());

        when(userService.getUserById(anyLong())).thenReturn(userDto);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForComment);
        when(bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(
                anyLong(),
                anyLong(),
                any()))
                .thenReturn(bookingsFromRepository);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDtoResult = itemService.createComment(1, 1, commentDto);
        assertEquals(1, commentDtoResult.getId());
        assertEquals("Comment text", commentDtoResult.getText());
    }

    @Test
    void createCommentTestThrowIllegalArgumentException() {
        UserDto userDto = new UserDto(1, "email@mail.ru", "NameUser");
        User owner1 = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForComment = new Item(1, "Hammer", "Big hammer", true, owner1);
        CommentDto commentDto = new CommentDto(0,  "Comment text", null, null);

        when(userService.getUserById(anyLong())).thenReturn(userDto);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForComment);
        when(bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(
                anyLong(),
                anyLong(),
                any()))
                .thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> itemService.createComment(1, 1, commentDto));
    }
}