package com.example.vbeat_mobile.backend.user.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.UserBackendException;
import com.example.vbeat_mobile.backend.user.VBeatUserModel;
import com.example.vbeat_mobile.viewmodel.UserViewModel;

import java.util.LinkedList;
import java.util.List;

public class UserRepository {
    private UserCache userCache = null;
    private static final String TAG = "UserRepository";

    private static class UserRepositoryInstanceHolder {
        private static UserRepository instance = new UserRepository();
    }

    public static UserRepository getInstance() {
        return UserRepositoryInstanceHolder.instance;
    }

    private UserRepository() {
        userCache = new UserCache();
    }

    public LiveData<UserViewModel> getUser(final String userId) {
        final MutableLiveData<UserViewModel> userViewModel = new MutableLiveData<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                VBeatUserModel userModel = userCache.get(userId);
                if (userModel == null) {
                    try {
                        userModel = FirebaseUserManager.getInstance().getUser(userId);
                    } catch (UserBackendException e) {
                        userViewModel.postValue(null);
                    }
                    userCache.save(userModel);
                }
                userViewModel.postValue(getViewModelFromModel(userModel));
            }
        }).start();

        return userViewModel;
    }

    public LiveData<UserViewModel> getCurrentUser() {
        return getUser(
                FirebaseUserManager.getInstance().getCurrentUser().getUserId()
        );
    }

    public LiveData<List<VBeatUserModel>> getUsers(final List<String> userIds) {
        final MutableLiveData<List<VBeatUserModel>> userModelList = new MutableLiveData<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // get all users that we can from cache
                List<VBeatUserModel> fetchedList = tryToLoadUsersFromCache(userIds);
                List<String> remainingUserIds = removeFetchedIds(fetchedList, userIds);

                // get remaining users and add them to the list
                if (remainingUserIds.size() > 0) {
                    boolean isSuccessful =
                            fetchRemainingUserIdsFromFirebase(fetchedList, remainingUserIds, userModelList);
                    // if we haven't managed to get user list
                    // fail the entire method
                    if (!isSuccessful) {
                        return;
                    }
                }

                userModelList.postValue(fetchedList);
            }
        }).start();

        return userModelList;
    }

    public void saveUsers(List<VBeatUserModel> userModels) {
        for (VBeatUserModel model : userModels) {
            userCache.save(model);
        }
    }

    private boolean fetchRemainingUserIdsFromFirebase(List<VBeatUserModel> fetchedList, List<String> remainingUserIds, MutableLiveData<List<VBeatUserModel>> userModelList) {
        try {
            List<VBeatUserModel> fromFirebase = FirebaseUserManager.getInstance().getUsers(remainingUserIds);

            // add new users to cache
            saveUsers(fromFirebase);

            fetchedList.addAll(
                    fromFirebase
            );
        } catch (UserBackendException e) {
            Log.e(TAG, "getUsers failed with exception", e);
            // set null unless we were able to get all users
            userModelList.postValue(null);
            return false;
        }

        return true;
    }

    private List<String> removeFetchedIds(List<VBeatUserModel> fetchedList, List<String> userIds) {
        for (VBeatUserModel model : fetchedList) {
            userIds.remove(model.getUserId());
        }

        return userIds;
    }

    private List<VBeatUserModel> tryToLoadUsersFromCache(List<String> userIds) {
        List<VBeatUserModel> cacheList = new LinkedList<>();
        for (String userId : userIds) {
            VBeatUserModel userModel = userCache.get(userId);
            if (userModel != null) {
                cacheList.add(userModel);
            }
        }

        return cacheList;
    }

    private UserViewModel getViewModelFromModel(VBeatUserModel userModel) {
        return new UserViewModel(
                userModel.getUserId(),
                userModel.getEmail(),
                userModel.getDisplayName());
    }
}
