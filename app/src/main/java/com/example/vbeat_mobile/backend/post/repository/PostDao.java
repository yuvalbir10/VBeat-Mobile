package com.example.vbeat_mobile.backend.post.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.vbeat_mobile.backend.post.VBeatPostModel;

import java.util.List;

@Dao
public interface PostDao {

    @Query("select * from VBeatPostModel")
    List<VBeatPostModel> getAll();

    @Query("select * from VBeatPostModel where postId == :postId")
    List<VBeatPostModel> getPost(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(VBeatPostModel ... posts);

    @Delete
    void delete(VBeatPostModel model);
}
