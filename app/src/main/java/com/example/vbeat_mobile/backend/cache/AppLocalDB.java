package com.example.vbeat_mobile.backend.cache;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.backend.post.repository.PostDao;

@Database(entities = {VBeatPostModel.class}, version =  1)
abstract class AppLocalDbRepository extends RoomDatabase{
    public abstract PostDao postDao();
}

public class AppLocalDB {
    public AppLocalDbRepository db;

    private static class ApplLocalDbInstanceHolder {
        private static AppLocalDB instance;
    }

    public static AppLocalDB getInstance(){
        return ApplLocalDbInstanceHolder.instance;
    }

    private AppLocalDB(Context c){
        db = Room.databaseBuilder(c, AppLocalDbRepository.class, "local_cache.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static void initialize(Context c){
        ApplLocalDbInstanceHolder.instance = new AppLocalDB(c);
    }
}
