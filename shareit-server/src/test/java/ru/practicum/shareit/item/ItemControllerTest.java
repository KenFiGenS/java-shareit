package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingDtoItemById;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

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

        Mockito.when(itemService.createItem(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(itemAfter);

        mockMvc.perform((MockMvcRequestBuilders.post("/items"))
                        .content(objectMapper.writeValueAsString(itemBefore))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1L), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemBefore.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemBefore.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemBefore.getAvailable())));

    }

    @SneakyThrows
    @Test
    void updateItem() {
        ItemDto itemDtoForUpdate = new ItemDto(0, null, "Very Big hammer", null, 0);
        ItemDto itemDtoAfterUpdate = new ItemDto(1, "Hammer", "Very Big hammer", true, 0);

        Mockito.when(itemService.updateItem(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(itemDtoAfterUpdate);

        mockMvc.perform((MockMvcRequestBuilders.patch("/items/1"))
                        .content(objectMapper.writeValueAsString(itemDtoForUpdate))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1L), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDtoAfterUpdate.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDtoForUpdate.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDtoAfterUpdate.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getItemById() {
        ItemDtoWithBookingAndComments item = getItemsForTest().get(0);
        Mockito.when(itemService.getItemById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(item);

        mockMvc.perform((MockMvcRequestBuilders.get("/items/1"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(item)));
    }

    @SneakyThrows
    @Test
    void getAllItemByOwner() {
        List<ItemDtoWithBookingAndComments> items = getItemsForTest();

        Mockito.when(itemService.getAllItemByOwner(ArgumentMatchers.anyLong())).thenReturn(items);

        mockMvc.perform((MockMvcRequestBuilders.get("/items"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(items)));
    }

    @SneakyThrows
    @Test
    void searchByNameAndDescription() {
        ItemDto item2 = new ItemDto(2, "Knife", "Small", true, 0);
        ItemDto item3 = new ItemDto(3, "Sword", "Sword of knight", true, 0);
        List<ItemDto> itemSearchResult = List.of(item2, item3);

        Mockito.when(itemService.search(ArgumentMatchers.anyString())).thenReturn(itemSearchResult);

        mockMvc.perform((MockMvcRequestBuilders.get("/items/search?text=kni"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemSearchResult)));
    }

    @SneakyThrows
    @Test
    void createComment() {
        CommentDto commentDto = new CommentDto(1, "Text", "Iva", LocalDateTime.now());
        CommentDto newCommentDto = new CommentDto(0, "Text", "Iva", LocalDateTime.now());

        Mockito.when(itemService.createComment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(commentDto);

        mockMvc.perform((MockMvcRequestBuilders.post("/items/1/comment"))
                        .content(objectMapper.writeValueAsString(newCommentDto))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(commentDto)));
    }
}