package com.example.vbeat_mobile.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

import com.example.vbeat_mobile.backend.user.VBeatUser;

public class PostViewModel extends ViewModel {
    private String postId;
    private String description;
    private String remoteImagePath;
    private String remoteMusicPath;
    private VBeatUser uploader;

    public PostViewModel(
            String postId,
            String description,
            String remoteImagePath,
            String remoteMusicPath,
            VBeatUser uploader
                         ) {
        this.postId = postId;
        this.description = description;
        this.remoteImagePath = remoteImagePath;
        this.remoteMusicPath = remoteMusicPath;
        this.uploader = uploader;
    }

    public String getPostId() {
        return postId;
    }

    public String getDescription() {
        return description;
    }

    public String getRemoteImagePath() {
        return remoteImagePath;
    }

    public String getRemoteMusicPath() {
        return remoteMusicPath;
    }

    public VBeatUser getUploader() {
        return uploader;
    }
}
