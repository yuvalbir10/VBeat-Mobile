package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.ViewModel;

public class PostViewModel extends ViewModel {
    private String postId;
    private String description;
    private String remoteImagePath;
    private String remoteMusicPath;
    private String uploaderId;

    public PostViewModel(
            String postId,
            String description,
            String remoteImagePath,
            String remoteMusicPath,
            String uploader
                         ) {
        this.postId = postId;
        this.description = description;
        this.remoteImagePath = remoteImagePath;
        this.remoteMusicPath = remoteMusicPath;
        this.uploaderId = uploader;
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

    public String getUploader() {
        return uploaderId;
    }
}
