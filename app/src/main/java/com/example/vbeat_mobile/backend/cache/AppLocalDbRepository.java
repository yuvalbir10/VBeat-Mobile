package com.example.vbeat_mobile.backend.cache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.backend.post.repository.PostDao;
import com.example.vbeat_mobile.backend.user.VBeatUserModel;
import com.example.vbeat_mobile.backend.user.repository.UserDao;

@Database(entities = {VBeatPostModel.class, VBeatUserModel.class}, version =  2)
abstract public class AppLocalDbRepository extends RoomDatabase {
    public abstract PostDao postDao();

    public abstract UserDao userDao();
}
