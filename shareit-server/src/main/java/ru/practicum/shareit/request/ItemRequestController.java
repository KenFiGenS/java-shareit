package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constans.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(Constants.REQUEST_HEADER_NAME) long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Выполняется запрос создания нового ItemRequest");
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(Constants.REQUEST_HEADER_NAME) long userId,
                                         @PathVariable long requestId) {
        log.info("Выполняется запрос получения информации о ItemRequest под id: {}", requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(Constants.REQUEST_HEADER_NAME) long userId) {
        log.info("Выполняется запрос получения информации о всех ItemRequest пользователя под id: {}", userId);
        return itemRequestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequests(@RequestHeader(Constants.REQUEST_HEADER_NAME) long userId,
                                            @RequestParam int from,
                                            @RequestParam int size) {
        log.info("Выполняется запрос страницы с индекса {}, размером {}, от пользователя {}", from, size, userId);
        return itemRequestService.getRequestsAll(userId, from, size);
    }
}
