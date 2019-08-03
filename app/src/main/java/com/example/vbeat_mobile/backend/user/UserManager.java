package com.example.vbeat_mobile.backend.user;

import com.example.vbeat_mobile.backend.user.UserRegistrationFailedException;
import com.example.vbeat_mobile.backend.user.VBeatUser;

public interface UserManager {
    boolean isUserLoggedIn();

    void createAccount(String email, String password) throws UserRegistrationFailedException;

    boolean deleteUser();

    VBeatUser getCurrentUser();

    void login(String email, String password) throws UserLoginFailedException;
}
