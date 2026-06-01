package org.example;

import java.util.List;
import java.util.stream.Collectors;

public class UserSelectionService {

    public List<User> getSelectableUsers(List<User> allUsers) {
        return allUsers;
    }

    public User selectUserByName(List<User> users, String name) {
        return users.stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public User selectUserById(List<User> users, String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Task> getTasksForUser(List<Task> tasks, User user) {
        return tasks.stream()
                .filter(task -> task.getOwner().equals(user.getName()))
                .collect(Collectors.toList());
    }
}
