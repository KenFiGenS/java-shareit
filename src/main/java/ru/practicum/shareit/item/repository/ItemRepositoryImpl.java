package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptionControllers.DataNotFound;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//@Slf4j
//@Repository
//@AllArgsConstructor
//@NoArgsConstructor
public class ItemRepositoryImpl  {
//    private Map<Long, List<Item>> itemsByUserId = new HashMap<>();
//    private Map<Long, Item> itemsByItemId = new HashMap<>();
//    private long currentId = 0;
//
//    @Override
//    public Item createItem(long userId, Item item) {
//        item.setId(increaseId());
//        List<Item> itemList = itemsByUserId.computeIfAbsent(userId, k -> new ArrayList<>());
//        itemList.add(item);
//        itemsByUserId.put(userId, itemList);
//        itemsByItemId.put(item.getId(), item);
//        return item;
//    }
//
//    @SneakyThrows
//    @Override
//    public Item updateItem(long userId, long id, Item item) {
//        List<Item> currentItemList = itemsByUserId.get(userId);
//        if (currentItemList == null) {
//            throw new DataNotFound("Список вещей данного позьзователя пуст");
//        }
//        Item currentItem = currentItemList.stream()
//                .filter(i -> i.getId() == id)
//                .findFirst()
//                .orElseThrow((Supplier<Throwable>) () ->
//                        new DataNotFound("У данного пользователя нет вещи под ID: " + id));
//        item.setId(currentItem.getId());
//        if (item.getName() != null && !item.getName().isBlank()) {
//            currentItem.setName(item.getName());
//        }
//        if (item.getDescription() != null && !item.getDescription().isBlank()) {
//            currentItem.setDescription(item.getDescription());
//        }
//        if (item.getAvailable() != null) {
//            currentItem.setAvailable(item.getAvailable());
//        }
//        return currentItem;
//    }
//
//    @Override
//    public Item getItemById(long userId, long id) {
//        return itemsByItemId.get(id);
//    }
//
//    @Override
//    public List<Item> getAllItemByUser(long userId) {
//        return itemsByUserId.get(userId);
//    }
//
//    @Override
//    public List<Item> search(String text) {
//        return this.itemsByUserId.values().stream()
//                .flatMap(List::stream)
//                .filter(item -> (item.getName().toLowerCase().contains(text) ||
//                        item.getDescription().toLowerCase().contains(text)))
//                .filter(Item::getAvailable)
//                .collect(Collectors.toList());
//    }
//
//
//    private long increaseId() {
//        return ++currentId;
//    }
}
