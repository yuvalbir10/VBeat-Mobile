package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.ViewModel;

import java.util.Date;

public class PostViewModel extends ViewModel {
    private String postId;
    private String description;
    private String remoteImagePath;
    private String remoteMusicPath;
    private String uploaderId;
    private Date uploadDate;

    public PostViewModel(
            String postId,
            String description,
            String remoteImagePath,
            String remoteMusicPath,
            String uploader,
            Date uploadDate
                         ) {
        this.postId = postId;
        this.description = description;
        this.remoteImagePath = remoteImagePath;
        this.remoteMusicPath = remoteMusicPath;
        this.uploaderId = uploader;
        this.uploadDate = uploadDate;
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

    public Date getUploadDate() {
        return uploadDate;
    }
}
