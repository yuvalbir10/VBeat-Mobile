package com.example.vbeat_mobile.backend.post;

import android.net.Uri;

public interface PostManager {
    void uploadPost(String description, Uri imageUri, Uri musicUri) throws UploadPostFailedException;
}
