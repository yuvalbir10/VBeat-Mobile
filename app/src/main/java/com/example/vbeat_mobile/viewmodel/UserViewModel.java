package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.ViewModel;

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
}
