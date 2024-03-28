package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
@NoArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Long, List<Item>> ITEMS = new HashMap<>();
    private long currentId = 0;

    @Override
    public Item createItem(long userId, Item item) {
        item.setId(increaseId());
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        ITEMS.put(userId, itemList);
        return item;
    }

    @SneakyThrows
    @Override
    public Item updateItem(long userId, long id, Item item) {
        List<Item> currentItemList = ITEMS.get(userId);
        if (currentItemList == null) {
            throw new SQLDataException("Список вещей данного позьзователя пуст");
        }
        Item currentItem = ITEMS.get(userId).stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElseThrow((Supplier<Throwable>) () -> new SQLDataException("У данного пользователя нет вещи под ID: " + id));
        item.setId(currentItem.getId());
        if (item.getName() != null) currentItem.setName(item.getName());
        if (item.getDescription() != null) currentItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) currentItem.setAvailable(item.getAvailable());
        return currentItem;
    }

    @Override
    public Item getItemById(long userId, long id) {
        return ITEMS.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId() == id)
                .findFirst().get();
    }

    @Override
    public List<Item> getAllItemByUser(long userId) {
        return ITEMS.get(userId);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> items = ITEMS.values().stream()
                .flatMap(List::stream)
                .filter(item -> (item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text)))
                .filter(item -> item.getAvailable().equals("true"))
                .collect(Collectors.toList());
        return items;
    }


    private long increaseId() {
        return ++currentId;
    }
}
