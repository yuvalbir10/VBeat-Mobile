package com.example.vbeat_mobile.backend.user;

public interface UserManager {
    boolean isUserLoggedIn();

    void createAccount(String email, String password) throws UserRegistrationFailedException;

    boolean deleteUser();

    VBeatUserModel getCurrentUser();

    void login(String email, String password) throws UserLoginFailedException;
}
