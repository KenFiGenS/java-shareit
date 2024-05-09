package ru.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.AppServer;

@SpringBootTest(classes = AppServer.class)
public class AppTest {
    @Test
    void contextLoads() {
    }
}
