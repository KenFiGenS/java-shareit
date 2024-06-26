package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

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
    @Mock
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository,
                userService,
                bookingRepository,
                commentRepository,
                itemRequestRepository
        );
    }

    @Test
    void createItemTest() {
        UserDto userDtoFromUserService = new UserDto(1, "email@mail.ru", "NameUser");
        ItemDto itemDtoBeforeSave = new ItemDto(0, "Hammer", "Big hammer", true, 0);
        Item itemAfterSave = new Item(1, "Hammer", "Big hammer", true, UserMapper.toUser(userDtoFromUserService), null);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(userDtoFromUserService);
        Mockito.when(itemRepository.save(ArgumentMatchers.any())).thenReturn(itemAfterSave);

        ItemDto itemDtoAfterSave = itemService.createItem(1, itemDtoBeforeSave);
        Assertions.assertEquals(1, itemDtoAfterSave.getId());
        Mockito.verify(itemRepository).save(ArgumentMatchers.any(Item.class));
    }

    @Test
    void updateItemTestThrowDataNotFoundException() {
        Assertions.assertThrows(
                DataNotFound.class,
                () -> itemService.updateItem(1, 1, new ItemDto(0, "Hammer", "Big hammer", true, 0))
        );
    }

    @Test
    void updateItemTest() {
        User owner = new User(1, "email@mail.ru", "NameUser");
        Item itemFromRepository = new Item(1, "Hammer", "Big hammer", true, owner, null);
        ItemDto itemDtoForUpdate = new ItemDto(0, "NewHammer", "Very Big hammer", true, 0);
        Item itemAfterUpdate = new Item(1, "NewHammer", "Very Big hammer", true, owner, null);

        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemFromRepository);
        Mockito.when(itemRepository.save(ArgumentMatchers.any())).thenReturn(itemAfterUpdate);

        ItemDto itemDtoUpdateResult = itemService.updateItem(1, 1, itemDtoForUpdate);
        Assertions.assertEquals(1, itemDtoUpdateResult.getId());
        Assertions.assertEquals("NewHammer", itemAfterUpdate.getName());
        Assertions.assertEquals("Very Big hammer", itemAfterUpdate.getDescription());
        Assertions.assertEquals(true, itemAfterUpdate.getAvailable());
        Mockito.verify(itemRepository).save(ArgumentMatchers.any(Item.class));
    }

    @Test
    void updateItemNameTest() {
        User owner = new User(1, "email@mail.ru", "NameUser");
        Item itemFromRepository = new Item(1, "Hammer", "Big hammer", true, owner, null);
        ItemDto itemDtoForUpdate = new ItemDto(0, "NewHammer", null, null, 0);
        Item itemAfterUpdate = new Item(1, "NewHammer", "Big hammer", true, owner, null);

        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemFromRepository);
        Mockito.when(itemRepository.save(ArgumentMatchers.any())).thenReturn(itemAfterUpdate);

        ItemDto itemDtoUpdateResult = itemService.updateItem(1, 1, itemDtoForUpdate);
        Assertions.assertEquals(1, itemDtoUpdateResult.getId());
        Assertions.assertEquals("NewHammer", itemAfterUpdate.getName());
        Assertions.assertEquals("Big hammer", itemAfterUpdate.getDescription());
        Assertions.assertEquals(true, itemAfterUpdate.getAvailable());
        Mockito.verify(itemRepository).save(ArgumentMatchers.any(Item.class));
    }

    @Test
    void updateItemDescriptionTest() {
        User owner = new User(1, "email@mail.ru", "NameUser");
        Item itemFromRepository = new Item(1, "Hammer", "Big hammer", true, owner, null);
        ItemDto itemDtoForUpdate = new ItemDto(0, null, "Very Big hammer", null, 0);
        Item itemAfterUpdate = new Item(1, "Hammer", "Very Big hammer", true, owner, null);

        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemFromRepository);
        Mockito.when(itemRepository.save(ArgumentMatchers.any())).thenReturn(itemAfterUpdate);

        ItemDto itemDtoUpdateResult = itemService.updateItem(1, 1, itemDtoForUpdate);
        Assertions.assertEquals(1, itemDtoUpdateResult.getId());
        Assertions.assertEquals("Hammer", itemAfterUpdate.getName());
        Assertions.assertEquals("Very Big hammer", itemAfterUpdate.getDescription());
        Assertions.assertEquals(true, itemAfterUpdate.getAvailable());
        Mockito.verify(itemRepository).save(ArgumentMatchers.any(Item.class));
    }

    @Test
    void updateItemAvailableTest() {
        User owner = new User(1, "email@mail.ru", "NameUser");
        Item itemFromRepository = new Item(1, "Hammer", "Big hammer", true, owner, null);
        ItemDto itemDtoForUpdate = new ItemDto(0, null, null, false, 0);
        Item itemAfterUpdate = new Item(1, "Hammer", "Big hammer", false, owner, null);

        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemFromRepository);
        Mockito.when(itemRepository.save(ArgumentMatchers.any())).thenReturn(itemAfterUpdate);

        ItemDto itemDtoUpdateResult = itemService.updateItem(1, 1, itemDtoForUpdate);
        Assertions.assertEquals(1, itemDtoUpdateResult.getId());
        Assertions.assertEquals("Hammer", itemAfterUpdate.getName());
        Assertions.assertEquals("Big hammer", itemAfterUpdate.getDescription());
        Assertions.assertEquals(false, itemAfterUpdate.getAvailable());
        Mockito.verify(itemRepository).save(ArgumentMatchers.any(Item.class));
    }

    @Test
    void getItemByIdTest() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        List<Comment> comments = List.of(
                new Comment(1, "Comment text", itemForBooking, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking, booker, LocalDateTime.now().minusDays(1))
        );

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(bookingsFromRepository);
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForBooking);
        Mockito.when(commentRepository.findAllByItemId(ArgumentMatchers.anyLong())).thenReturn(comments);

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        Assertions.assertEquals(1, itemDtoWithBookingAndComments.getId());
        Assertions.assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        Assertions.assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        Assertions.assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        Assertions.assertEquals(2, itemDtoWithBookingAndComments.getLastBooking().getId());
        Assertions.assertEquals(1, itemDtoWithBookingAndComments.getNextBooking().getId());
        Assertions.assertEquals(2, itemDtoWithBookingAndComments.getComments().size());
    }

    @Test
    void getItemByIdTestNextBookingIsEmpty() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        List<Comment> comments = List.of(
                new Comment(1, "Comment text", itemForBooking, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking, booker, LocalDateTime.now().minusDays(1))
        );

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(bookingsFromRepository);
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForBooking);
        Mockito.when(commentRepository.findAllByItemId(ArgumentMatchers.anyLong())).thenReturn(comments);

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        Assertions.assertEquals(1, itemDtoWithBookingAndComments.getId());
        Assertions.assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        Assertions.assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        Assertions.assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        Assertions.assertEquals(2, itemDtoWithBookingAndComments.getLastBooking().getId());
        Assertions.assertNull(itemDtoWithBookingAndComments.getNextBooking());
        Assertions.assertEquals(2, itemDtoWithBookingAndComments.getComments().size());
    }

    @Test
    void getItemByIdTestLastBookingIsEmpty() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED)
        );
        List<Comment> comments = List.of(
                new Comment(1, "Comment text", itemForBooking, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking, booker, LocalDateTime.now().minusDays(1))
        );

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(bookingsFromRepository);
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForBooking);
        Mockito.when(commentRepository.findAllByItemId(ArgumentMatchers.anyLong())).thenReturn(comments);

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        Assertions.assertEquals(1, itemDtoWithBookingAndComments.getId());
        Assertions.assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        Assertions.assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        Assertions.assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        Assertions.assertNull(itemDtoWithBookingAndComments.getLastBooking());
        Assertions.assertEquals(1, itemDtoWithBookingAndComments.getNextBooking().getId());
        Assertions.assertEquals(2, itemDtoWithBookingAndComments.getComments().size());
    }

    @Test
    void getItemByIdTestBookingIsEmptyWithComments() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);

        List<Comment> comments = List.of(
                new Comment(1, "Comment text", itemForBooking, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking, booker, LocalDateTime.now().minusDays(1))
        );

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(List.of());
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForBooking);
        Mockito.when(commentRepository.findAllByItemId(ArgumentMatchers.anyLong())).thenReturn(comments);

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        Assertions.assertEquals(1, itemDtoWithBookingAndComments.getId());
        Assertions.assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        Assertions.assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        Assertions.assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        Assertions.assertNull(itemDtoWithBookingAndComments.getLastBooking());
        Assertions.assertNull(itemDtoWithBookingAndComments.getNextBooking());
        Assertions.assertEquals(2, itemDtoWithBookingAndComments.getComments().size());
    }

    @Test
    void getItemByIdTestBookingWithCommentsIsEmpty() {
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(List.of());
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForBooking);
        Mockito.when(commentRepository.findAllByItemId(ArgumentMatchers.anyLong())).thenReturn(List.of());

        ItemDtoWithBookingAndComments itemDtoWithBookingAndComments = itemService.getItemById(2, 1);
        Assertions.assertEquals(1, itemDtoWithBookingAndComments.getId());
        Assertions.assertEquals("Hammer", itemDtoWithBookingAndComments.getName());
        Assertions.assertEquals("Big hammer", itemDtoWithBookingAndComments.getDescription());
        Assertions.assertEquals(true, itemDtoWithBookingAndComments.getAvailable());
        Assertions.assertNull(itemDtoWithBookingAndComments.getLastBooking());
        Assertions.assertNull(itemDtoWithBookingAndComments.getNextBooking());
        Assertions.assertEquals(List.of(), itemDtoWithBookingAndComments.getComments());
    }

    @Test
    void getAllItemByOwner() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner1 = new User(2, "email2@mail.ru", "NameUser2");
        User owner2 = new User(3, "email3@mail.ru", "NameUser3");
        Item itemForBooking1 = new Item(1, "Hammer", "Big hammer", true, owner1, null);
        Item itemForBooking2 = new Item(2, "Glasses", "Big Glasses", true, owner1, null);
        Item itemForBooking3 = new Item(3, "Camera", "Professional camera", true, owner2, null);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking1, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking1, booker, BookingStatus.APPROVED)
        );
        List<Comment> comments = List.of(
                new Comment(1, "Comment text", itemForBooking1, booker, LocalDateTime.now()),
                new Comment(2, "Comment text2", itemForBooking1, booker, LocalDateTime.now().minusDays(1))
        );
        List<Item> itemsFromRepository = List.of(itemForBooking1, itemForBooking2);

        Mockito.when(itemRepository.findItemByOwnerId(ArgumentMatchers.anyLong())).thenReturn(itemsFromRepository);
        Mockito.when(bookingRepository.findByItemId(1L)).thenReturn(bookingsFromRepository);
        Mockito.when(bookingRepository.findByItemId(2L)).thenReturn(List.of());
        Mockito.when(itemRepository.getReferenceById(1L)).thenReturn(itemForBooking1);
        Mockito.when(itemRepository.getReferenceById(2L)).thenReturn(itemForBooking2);
        Mockito.when(commentRepository.findAllByItemId(1L)).thenReturn(comments);
        Mockito.when(commentRepository.findAllByItemId(2L)).thenReturn(List.of());

        List<ItemDtoWithBookingAndComments> itemDtoResult = itemService.getAllItemByOwner(1);
        Assertions.assertEquals(2, itemDtoResult.size());
    }

    @Test
    void searchTest() {
        User user = new User(0, "NameUser@mail.ru", "NameUser");
        Item item1 = new Item(1, "Hammer", "Big hammer", true, user, null);
        Item item2 = new Item(2, "Knife", "Small", true, user, null);
        Item item3 = new Item(3, "Sword", "Sword of knight", true, user, null);
        List<Item> itemSearchResult = List.of(item2, item3);

        Mockito.when(itemRepository.searchByNameAndDescription(ArgumentMatchers.anyString())).thenReturn(itemSearchResult);

        List<ItemDto> searchResult = itemService.search("Kni");
        Assertions.assertEquals(2, searchResult.size());
        Assertions.assertEquals(2, searchResult.get(0).getId());
        Assertions.assertEquals(3, searchResult.get(1).getId());
    }

    @Test
    void createCommentTest() {
        UserDto userDto = new UserDto(1, "email@mail.ru", "NameUser");
        User user = new User(1, "email@mail.ru", "NameUser");
        User owner1 = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForComment = new Item(1, "Hammer", "Big hammer", true, owner1, null);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForComment, user, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForComment, user, BookingStatus.APPROVED)
        );
        CommentDto commentDto = new CommentDto(0, "Comment text", null, null);
        Comment comment = new Comment(1, "Comment text", itemForComment, user, LocalDateTime.now());

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(userDto);
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForComment);
        Mockito.when(bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(
                        ArgumentMatchers.anyLong(),
                        ArgumentMatchers.anyLong(),
                        ArgumentMatchers.any()))
                .thenReturn(bookingsFromRepository);
        Mockito.when(commentRepository.save(ArgumentMatchers.any())).thenReturn(comment);

        CommentDto commentDtoResult = itemService.createComment(1, 1, commentDto);
        Assertions.assertEquals(1, commentDtoResult.getId());
        Assertions.assertEquals("Comment text", commentDtoResult.getText());
    }

    @Test
    void createCommentTestThrowIllegalArgumentException() {
        UserDto userDto = new UserDto(1, "email@mail.ru", "NameUser");
        User owner1 = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForComment = new Item(1, "Hammer", "Big hammer", true, owner1, null);
        CommentDto commentDto = new CommentDto(0, "Comment text", null, null);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(userDto);
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForComment);
        Mockito.when(bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(
                        ArgumentMatchers.anyLong(),
                        ArgumentMatchers.anyLong(),
                        ArgumentMatchers.any()))
                .thenReturn(List.of());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> itemService.createComment(1, 1, commentDto));
    }
}