package com.example.smartproperty.common;

import com.example.smartproperty.entity.User;

public final class AuthContext {

    private static final ThreadLocal<User> HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setUser(User user) {
        HOLDER.set(user);
    }

    public static User getUser() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        User user = HOLDER.get();
        return user == null ? null : user.getId();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
