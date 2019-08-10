package com.example.vbeat_mobile.UI.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

import com.example.vbeat_mobile.backend.user.VBeatUser;

public class PostViewModel extends ViewModel {
    private String postId;
    private String description;
    private Bitmap postImage;
    private byte[] musicBytes;
    private VBeatUser uploader;

    public PostViewModel(
            String postId,
            String description,
            Bitmap postImage,
            byte[] musicBytes,
            VBeatUser uploader
                         ) {
        this.postId = postId;
        this.description = description;
        this.postImage = postImage;
        this.musicBytes = musicBytes;
        this.uploader = uploader;
    }

    public String getPostId() {
        return postId;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getPostImage() {
        return postImage;
    }

    public byte[] getMusicBytes() {
        return musicBytes;
    }

    public VBeatUser getUploader() {
        return uploader;
    }
}
