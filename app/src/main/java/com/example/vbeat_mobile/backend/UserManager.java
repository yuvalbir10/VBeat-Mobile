package com.example.vbeat_mobile.backend;

public interface UserManager {
    void login(String username, String password);

    void signUp(String email, String username, String password);
}
