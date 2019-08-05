package com.example.vbeat_mobile.backend.post;

import android.net.Uri;

import java.util.List;

public interface PostManager<T> {
    VBeatPost uploadPost(String description, Uri imageUri, Uri musicUri) throws UploadPostFailedException;

    VBeatPost getPost(String postId);

    VBeatPostCollection<T> getPosts(T cursor, int limit);
}
