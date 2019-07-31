package com.example.vbeat_mobile.backend;

public interface UserManager {
    boolean isUserLoggedIn();

    boolean createAccount(String email, String password);
}
