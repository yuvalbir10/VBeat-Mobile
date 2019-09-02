package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.vbeat_mobile.backend.user.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private String userId;
    private String email;
    private String displayName;


    public UserViewModel(String userId, String email, String displayName) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static LiveData<UserViewModel> getUser(String userId) {
        return UserRepository.getInstance().getUser(userId);
    }

    public static boolean login(String username, String password) {
        return UserRepository.getInstance().login(username, password);
    }

    public static boolean isLoggedIn(){
        return UserRepository.getInstance().isLoggedIn();
    }
}
