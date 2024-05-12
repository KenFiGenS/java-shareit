package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.userDto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

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
        ItemDto item = new ItemDto(1, "Hammer", "Big hammer", true, 0);
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

        Mockito.when(bookingService.createBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(bookingDto);

        mockMvc.perform((MockMvcRequestBuilders.post("/bookings"))
                        .content(objectMapper.writeValueAsString(bookingDtoForCreate))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @SneakyThrows
    @Test
    void confirmationBookingTest() {
        BookingDto bookingDto = getBookingForTest().get(1);

        Mockito.when(bookingService.confirmationBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform((MockMvcRequestBuilders.patch("/bookings/2?approved=true"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        BookingDto bookingDto = getBookingForTest().get(1);

        Mockito.when(bookingService.getBookingById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(bookingDto);

        mockMvc.perform((MockMvcRequestBuilders.get("/bookings/2"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByBookerTest() {
        List<BookingDto> bookings = getBookingForTest();

        Mockito.when(bookingService.getAllBookingsByBooker(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(bookings);

        mockMvc.perform((MockMvcRequestBuilders.get("/bookings?state=ALL"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookings)));
    }

    @SneakyThrows
    @Test
    void getAllBookingByOwnerTest() {
        List<BookingDto> bookings = getBookingForTest();

        Mockito.when(bookingService.getAllBookingsByOwner(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(bookings);

        mockMvc.perform((MockMvcRequestBuilders.get("/bookings/owner?state=ALL"))
                        .header(Constants.REQUEST_HEADER_NAME, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookings)));
    }
}