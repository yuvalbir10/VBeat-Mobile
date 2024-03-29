package com.example.vbeat_mobile.backend.post.repository;

import android.util.Log;

import com.example.vbeat_mobile.backend.cache.AppLocalDB;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;

import java.util.List;

public class PostCache {
    private static final String TAG = "PostCache";
    // cache is empty and not implemented for now
    public VBeatPostModel getPost(String postId){
        List<VBeatPostModel> postList = AppLocalDB.getInstance().db.postDao().getPost(postId);

        if(postList.size() == 0) {
            return null;
        } else {
            return postList.get(0);
        }

    }

    // cache is currently not implemented
    public void savePost(VBeatPostModel post) {
        AppLocalDB.getInstance().db.postDao().insertAll(post);
    }

    public List<VBeatPostModel> getAllPostsInCache(){
        return AppLocalDB.getInstance().db.postDao().getAll();
    }

    public void updatePost(String postId, String descritpion) {
        VBeatPostModel postModel = getPost(postId);
        if(postModel == null){
            Log.e(TAG, "updatePost: postModel == null");
            return;
        }
        postModel.setDescription(descritpion);
        savePost(postModel);
    }
}
