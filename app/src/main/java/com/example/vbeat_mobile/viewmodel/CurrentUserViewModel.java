package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.vbeat_mobile.backend.user.repository.UserRepository;
import com.google.android.gms.common.UserRecoverableException;

public class CurrentUserViewModel extends ViewModel {
    // we don't care about caching the current user data
    // we'll just fetch it every time and UserRepository
    // will cache it if needed
    // user is not editable anyway
    public static LiveData<UserViewModel> getCurrentUser(){
        return UserRepository.getInstance().getCurrentUser();
    }
}
