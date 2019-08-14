package com.example.vbeat_mobile.backend.user.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.VBeatUserModel;
import com.example.vbeat_mobile.viewmodel.UserViewModel;

public class UserRepository {
    private UserCache userCache = null;

    private static class UserRepositoryInstanceHolder {
        private static UserRepository instance = new UserRepository();
    }

    private UserRepository(){
        userCache = new UserCache();
    }

    public LiveData<UserViewModel> getUser(final String userId) {
        final MutableLiveData<UserViewModel> userViewModel = new MutableLiveData<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                VBeatUserModel userModel = userCache.get(userId);
                if(userModel == null) {
                    userModel = FirebaseUserManager.getInstance().getUser(userId);
                    userCache.save(userModel);
                }
                userViewModel.setValue(getViewModelFromModel(userModel));
            }
        }).start();

        return userViewModel;
    }

    private UserViewModel getViewModelFromModel(VBeatUserModel userModel) {
        return new UserViewModel(
                userModel.getUserId(),
                userModel.getEmail(),
                userModel.getDisplayName());
    }
}
