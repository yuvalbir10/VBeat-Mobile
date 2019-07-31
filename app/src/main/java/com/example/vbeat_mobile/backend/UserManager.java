package com.example.vbeat_mobile.backend;

import com.example.vbeat_mobile.backend.user.VBeatUser;

public interface UserManager {
    boolean isUserLoggedIn();

    boolean createAccount(String email, String password);

    boolean deleteUser();

    VBeatUser getCurrentUser();
}
