package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingService bookingService;


    @Test
    @DirtiesContext
    void getAllItemByOwner() {
        UserDto booker = userService.create(new UserDto(0, "email@mail.ru", "NameUser"));
        UserDto owner1 = userService.create(new UserDto(0, "email2@mail.ru", "NameUser2"));
        UserDto owner2 = userService.create(new UserDto(0, "email3@mail.ru", "NameUser3"));
        Item itemForBooking1 = new Item(1, "Hammer", "Big hammer", true, UserMapper.toUser(owner1));
        Item itemForBooking2 = new Item(2, "Glasses", "Big Glasses", true, UserMapper.toUser(owner1));
        Item itemForBooking3 = new Item(3, "Camera", "Professional camera", true, UserMapper.toUser(owner2));
        long itemId1 = itemRepository.save(itemForBooking1).getId();
        itemRepository.save(itemForBooking2).getId();
        itemRepository.save(itemForBooking3).getId();


        bookingService.createBooking(booker.getId(), new BookingDtoCreate(
                0L,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemId1
        ));

        bookingService.createBooking(booker.getId(), new BookingDtoCreate(
                0L,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemId1
        ));

        itemService.createComment(
                booker.getId(),
                itemId1,
                new CommentDto(0, "Text comment", null, null
                ));

        itemService.createComment(
                booker.getId(),
                itemId1,
                new CommentDto(0, "Text comment", null, null
                ));

        List<ItemDtoWithBookingAndComments> itemsFromRepository = itemService.getAllItemByOwner(2);
        assertEquals(2, itemsFromRepository.size());
        assertEquals(2, itemsFromRepository.get(0).getComments().size());
        assertNotNull(itemsFromRepository.get(0).getNextBooking());
        assertNotNull(itemsFromRepository.get(0).getLastBooking());
    }
}