package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoItemById;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.constans.Constants.REQUEST_HEADER_NAME;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;

    private List<ItemDtoWithBookingAndComments> getItemsForTest() {
        return List.of(
                new ItemDtoWithBookingAndComments(
                        1,
                        "Lamp",
                        "White light",
                        true,
                        new BookingDtoItemById(
                                1L,
                                LocalDateTime.now().minusDays(4),
                                LocalDateTime.now().plusDays(2),
                                BookingStatus.APPROVED,
                                1L),
                        new BookingDtoItemById(
                                2L,
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(2),
                                BookingStatus.APPROVED,
                                1L),
                        List.of(
                                new CommentDto(1,
                                        "Text",
                                        "Iva",
                                        LocalDateTime.now())
                        )),
                new ItemDtoWithBookingAndComments(
                        2,
                        "Hammer",
                        "Big Hammer",
                        true,
                        null,
                        null,
                        List.of()
                ));
    }


    @SneakyThrows
    @Test
    void createItem() {
        ItemDto itemAfter = new ItemDto(1, "Hammer", "Big hammer", true, 0);
        ItemDto itemBefore = new ItemDto(0, "Hammer", "Big hammer", true, 0);

        when(itemService.createItem(anyLong(), any())).thenReturn(itemAfter);

        mockMvc.perform((post("/items"))
                        .content(objectMapper.writeValueAsString(itemBefore))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemBefore.getName())))
                .andExpect(jsonPath("$.description", is(itemBefore.getDescription())))
                .andExpect(jsonPath("$.available", is(itemBefore.getAvailable())));

    }

    @SneakyThrows
    @Test
    void updateItem() {
        ItemDto itemDtoForUpdate = new ItemDto(0, null, "Very Big hammer", null, 0);
        ItemDto itemDtoAfterUpdate = new ItemDto(1, "Hammer", "Very Big hammer", true, 0);

        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDtoAfterUpdate);

        mockMvc.perform((patch("/items/1"))
                        .content(objectMapper.writeValueAsString(itemDtoForUpdate))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoAfterUpdate.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForUpdate.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoAfterUpdate.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getItemById() {
        ItemDtoWithBookingAndComments item = getItemsForTest().get(0);
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);

        mockMvc.perform((get("/items/1"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @SneakyThrows
    @Test
    void getAllItemByOwner() {
        List<ItemDtoWithBookingAndComments> items = getItemsForTest();

        when(itemService.getAllItemByOwner(anyLong())).thenReturn(items);

        mockMvc.perform((get("/items"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @SneakyThrows
    @Test
    void searchByNameAndDescription() {
        ItemDto item2 = new ItemDto(2, "Knife", "Small", true, 0);
        ItemDto item3 = new ItemDto(3, "Sword", "Sword of knight", true, 0);
        List<ItemDto> itemSearchResult = List.of(item2, item3);

        when(itemService.search(anyString())).thenReturn(itemSearchResult);

        mockMvc.perform((get("/items/search?text=kni"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemSearchResult)));
    }

    @SneakyThrows
    @Test
    void createComment() {
        CommentDto commentDto = new CommentDto(1, "Text", "Iva", LocalDateTime.now());
        CommentDto newCommentDto = new CommentDto(0, "Text", "Iva", LocalDateTime.now());

        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mockMvc.perform((post("/items/1/comment"))
                        .content(objectMapper.writeValueAsString(newCommentDto))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }
}