package com.example.vbeat_mobile.backend.user.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.vbeat_mobile.backend.user.VBeatUserModel;

import java.util.List;

@Dao
public interface UserDao {
    @Query("select * from VBeatUserModel where userId == :userId")
    List<VBeatUserModel> getUser(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(VBeatUserModel... users);
}
