package com.example.vbeat_mobile.backend.post;

import android.net.Uri;

import com.example.vbeat_mobile.backend.comment.CommentException;

public interface PostManager<T> {
    VBeatPostModel uploadPost(String description, Uri imageUri, Uri musicUri) throws UploadPostFailedException;

    VBeatPostModel getPost(String postId);

    VBeatPostCollection<T> getPosts(T cursor, int limit);

    void deletePost(String postId) throws DeletePostException, CommentException;
}
