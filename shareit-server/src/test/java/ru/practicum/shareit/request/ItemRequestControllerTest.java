package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

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

        Mockito.when(itemRequestService.createItemRequest(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(itemRequestAfter);

        mockMvc.perform((MockMvcRequestBuilders.post("/requests"))
                        .content(objectMapper.writeValueAsString(itemRequestBefore))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestAfter)));
    }

    @SneakyThrows
    @Test
    void getRequestByIdTest() {
        User user = new User(1, "email@mail.ru", "NameUser");
        ItemRequestDto itemRequestAfter = new ItemRequestDto(1, "Хотел бы воспользоваться щёткой для обуви", user, LocalDateTime.now(), null);

        Mockito.when(itemRequestService.getRequestById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(itemRequestAfter);

        mockMvc.perform((MockMvcRequestBuilders.get("/requests/1"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestAfter)));
    }

    @SneakyThrows
    @Test
    void getRequestsTest() {
        User user = new User(1, "email@mail.ru", "NameUser");
        ItemRequestDto itemRequestAfter = new ItemRequestDto(1, "Хотел бы воспользоваться щёткой для обуви", user, LocalDateTime.now(), null);
        List<ItemRequestDto> itemRequestsFromService = List.of(itemRequestAfter);

        Mockito.when(itemRequestService.getRequestsByUser(ArgumentMatchers.anyLong())).thenReturn(List.of(itemRequestAfter));

        mockMvc.perform((MockMvcRequestBuilders.get("/requests"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestsFromService)));
    }

    @SneakyThrows
    @Test
    void testGetRequestsTest() {
        User user = new User(1, "email@mail.ru", "NameUser");
        ItemRequestDto itemRequestAfter = new ItemRequestDto(1, "Хотел бы воспользоваться щёткой для обуви", user, LocalDateTime.now(), null);
        List<ItemRequestDto> itemRequestsFromService = List.of(itemRequestAfter);

        Mockito.when(itemRequestService.getRequestsAll(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(List.of(itemRequestAfter));

        mockMvc.perform((MockMvcRequestBuilders.get("/requests/all?from=0&size=5"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestsFromService)));
    }

    @SneakyThrows
    @Test
    void testGetRequestsTestThrow() {
        Mockito.when(itemRequestService.getRequestsAll(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(List.of());

        mockMvc.perform((MockMvcRequestBuilders.get("/requests/all"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(List.of())));
    }
}