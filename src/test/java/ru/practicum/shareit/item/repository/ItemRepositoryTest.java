package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void searchByNameAndDescriptionTest() {
        User user = new User(0, "NameUser@mail.ru", "NameUser");
        userRepository.save(user);
        Item item1 = new Item(0, "Hammer", "Big hammer", true, user, null);
        Item item2 = new Item(0, "Knife", "Small", true, user, null);
        Item item3 = new Item(0, "Sword", "Sword of knight", true, user, null);
        itemRepository.save(item1);
        long item2Id = itemRepository.save(item2).getId();
        long item3Id = itemRepository.save(item3).getId();


        List<Item> searchResult = itemRepository.searchByNameAndDescription("Kni");

        Assertions.assertEquals(2, searchResult.size());
        Assertions.assertEquals(item2Id, searchResult.get(0).getId());
        Assertions.assertEquals(item3Id, searchResult.get(1).getId());
    }
}