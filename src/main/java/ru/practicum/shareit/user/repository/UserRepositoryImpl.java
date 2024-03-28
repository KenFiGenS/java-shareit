package ru.practicum.shareit.user.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@NoArgsConstructor
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    long userId = 0;

    private Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        long currentId = increaseUserId();
        user.setId(currentId);
        users.put(currentId, user);
        return user;
    }

    @Override
    public User patch(long id, User user) {
        User currentUserByUpdate = getUserById(id);
        String updateName = user.getName();
        String updateEmail = user.getEmail();
        if (updateName != null && updateEmail != null) {
            currentUserByUpdate.setName(updateName);
            currentUserByUpdate.setEmail(updateEmail);
        } else if (updateName != null) {
            currentUserByUpdate.setName(updateName);
        } else {
            currentUserByUpdate.setEmail(updateEmail);
        }
        return currentUserByUpdate;
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void removeUser(long id) {
        users.remove(id);
    }

    private Long increaseUserId() {
        return ++userId;
    }
}
