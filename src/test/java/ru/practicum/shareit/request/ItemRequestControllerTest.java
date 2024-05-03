package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constans.Constants.REQUEST_HEADER_NAME;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void createRequestTest() {
        User user = new User(1, "email@mail.ru", "NameUser");
        ItemRequestDto itemRequestBefore = new ItemRequestDto(0, "Хотел бы воспользоваться щёткой для обуви", null, null, null);
        ItemRequestDto itemRequestAfter = new ItemRequestDto(1, "Хотел бы воспользоваться щёткой для обуви", user, LocalDateTime.now(), null);

        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestAfter);

        mockMvc.perform((post("/requests"))
                        .content(objectMapper.writeValueAsString(itemRequestBefore))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestAfter)));
    }

    @SneakyThrows
    @Test
    void createRequestTestThrowException() {
        ItemRequestDto itemRequestBefore = new ItemRequestDto(0, null, null, null, null);

        mockMvc.perform((post("/requests"))
                        .content(objectMapper.writeValueAsString(itemRequestBefore))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getRequestByIdTest() {
        User user = new User(1, "email@mail.ru", "NameUser");
        ItemRequestDto itemRequestAfter = new ItemRequestDto(1, "Хотел бы воспользоваться щёткой для обуви", user, LocalDateTime.now(), null);

        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestAfter);

        mockMvc.perform((get("/requests/1"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestAfter)));
    }

    @SneakyThrows
    @Test
    void getRequestsTest() {
        User user = new User(1, "email@mail.ru", "NameUser");
        ItemRequestDto itemRequestAfter = new ItemRequestDto(1, "Хотел бы воспользоваться щёткой для обуви", user, LocalDateTime.now(), null);
        List<ItemRequestDto> itemRequestsFromService = List.of(itemRequestAfter);

        when(itemRequestService.getRequestsByUser(anyLong())).thenReturn(List.of(itemRequestAfter));

        mockMvc.perform((get("/requests"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestsFromService)));
    }

    @SneakyThrows
    @Test
    void testGetRequestsTest() {
        User user = new User(1, "email@mail.ru", "NameUser");
        ItemRequestDto itemRequestAfter = new ItemRequestDto(1, "Хотел бы воспользоваться щёткой для обуви", user, LocalDateTime.now(), null);
        List<ItemRequestDto> itemRequestsFromService = List.of(itemRequestAfter);

        when(itemRequestService.getRequestsAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestAfter));

        mockMvc.perform((get("/requests/all?from=0&size=5"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestsFromService)));
    }

    @SneakyThrows
    @Test
    void testGetRequestsTestThrow() {
        when(itemRequestService.getRequestsAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform((get("/requests/all"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }
}