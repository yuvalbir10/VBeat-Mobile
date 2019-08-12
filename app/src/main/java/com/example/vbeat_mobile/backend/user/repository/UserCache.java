package com.example.vbeat_mobile.backend.user.repository;

import com.example.vbeat_mobile.backend.cache.AppLocalDB;
import com.example.vbeat_mobile.backend.user.VBeatUserModel;

import java.util.List;

public class UserCache {
    public VBeatUserModel get(String userId){
        List<VBeatUserModel> user = AppLocalDB.getInstance().db.userDao().getUser(userId);

        if(user.size() == 0){
            return null;
        }

        return user.get(0);
    }

    public void save(VBeatUserModel userModel) {
        AppLocalDB.getInstance().db.userDao().insertAll(userModel);
    }
}
