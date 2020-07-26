package com.example.util;

import com.example.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserThreadLocal {

    private static final ThreadLocal<User> users = new ThreadLocal<>();

    public void addUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void remove() {
        users.remove();
    }

}
