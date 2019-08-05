package com.example.vbeat_mobile.backend.post;

import android.net.Uri;

import java.util.List;

public class FirebasePostManager implements PostManager<String> {
    @Override
    public VBeatPost uploadPost(String description, Uri imageUri, Uri musicUri) throws UploadPostFailedException {
        return null;
    }

    @Override
    public VBeatPost getPost(String postId) {
        return null;
    }

    @Override
    public VBeatPostCollection<String> getPosts(String cursor, int limit) {
        return null;
    }
}
