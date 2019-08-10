package com.example.vbeat_mobile.backend.cache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.backend.post.repository.PostDao;

@Database(entities = {VBeatPostModel.class}, version =  1)
abstract public class AppLocalDbRepository extends RoomDatabase {
    public abstract PostDao postDao();
}
