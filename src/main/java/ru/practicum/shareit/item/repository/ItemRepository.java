package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemByOwnerId(long id);

//    @Query("SELECT i " +
//            "from ITEMS i " +
//            "where i.name like concat(?1, '%') " +
//            "OR i.description like concat(?1, '%')")
//    List<Item> findItemByNameAndDescription(String text);
}
