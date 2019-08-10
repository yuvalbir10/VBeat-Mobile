package com.example.vbeat_mobile.backend.post.repository;

import com.example.vbeat_mobile.backend.cache.AppLocalDB;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;

public class PostCache {
    // cache is empty and not implemented for now
    public VBeatPostModel getPost(String postId){
        return null;
    }

    // cache is currently not implemented
    public void savePost(VBeatPostModel post) {
        return;
    }
}
