package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.userDto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constans.Constants.REQUEST_HEADER_NAME;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;

    private List<BookingDto> getBookingForTest() {
        UserDto booker = new UserDto(1, "Lol@mail.ru", "Lola");
        ItemDto item = new ItemDto(1, "Hammer", "Big hammer", true);
        BookingDto bookingDto1 = new BookingDto(
                1L,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED,
                booker,
                item);
        BookingDto bookingDto2 = new BookingDto(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED,
                booker,
                item);
        return List.of(bookingDto1, bookingDto2);
    }

    @SneakyThrows
    @Test
    void createBookingTest() {
        BookingDto bookingDto = getBookingForTest().get(1);
        BookingDtoCreate bookingDtoForCreate = new BookingDtoCreate(
                0,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                1);

        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDto);

        mockMvc.perform((post("/bookings"))
                        .content(objectMapper.writeValueAsString(bookingDtoForCreate))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @SneakyThrows
    @Test
    void createBookingTestValidationMethodArgumentNotValidException() {
        BookingDto bookingDto = getBookingForTest().get(0);
        BookingDtoCreate bookingDtoForCreate = new BookingDtoCreate(
                0,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                1);

        mockMvc.perform((post("/bookings"))
                        .content(objectMapper.writeValueAsString(bookingDtoForCreate))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void confirmationBookingTest() {
        BookingDto bookingDto = getBookingForTest().get(1);

        when(bookingService.confirmationBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform((patch("/bookings/2?approved=true"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        BookingDto bookingDto = getBookingForTest().get(1);

        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform((get("/bookings/2"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByBookerTest() {
        List<BookingDto> bookings = getBookingForTest();

        when(bookingService.getAllBookingsByBooker(anyLong(), anyString())).thenReturn(bookings);

        mockMvc.perform((get("/bookings?state=ALL"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
    }

    @SneakyThrows
    @Test
    void getAllBookingByOwnerTest() {
        List<BookingDto> bookings = getBookingForTest();

        when(bookingService.getAllBookingsByOwner(anyLong(), anyString())).thenReturn(bookings);

        mockMvc.perform((get("/bookings/owner?state=ALL"))
                        .header(REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));
    }
}