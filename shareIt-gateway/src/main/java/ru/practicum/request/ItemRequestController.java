package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constans.Constants;
import ru.practicum.exception.Marker;
import ru.practicum.request.dto.ItemRequestDto;


import java.util.List;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private RequestClient requestClient;


    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(Constants.REQUEST_HEADER_NAME) long userId,
                                        @Validated(Marker.OnCreate.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Выполняется запрос создания нового ItemRequest");
        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(Constants.REQUEST_HEADER_NAME) long userId,
                                         @PathVariable long requestId) {
        log.info("Выполняется запрос получения информации о ItemRequest под id: {}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(Constants.REQUEST_HEADER_NAME) long userId) {
        log.info("Выполняется запрос получения информации о всех ItemRequest пользователя под id: {}", userId);
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequests(@RequestHeader(Constants.REQUEST_HEADER_NAME) long userId,
                                            @RequestParam int from,
                                            @RequestParam int size) {
        log.info("Выполняется запрос страницы с индекса {}, размером {}, от пользователя {}", from, size, userId);
        return requestClient.getRequestsAll(userId, from, size);
    }
}
