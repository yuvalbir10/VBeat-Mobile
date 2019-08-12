package com.example.vbeat_mobile.backend.post;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VBeatPostModel {
    protected String description;
    protected String remoteImagePath;
    protected String remoteMusicPath;
    protected String uploaderId;

    @PrimaryKey
    @NonNull
    protected String postId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemoteImagePath() {
        return remoteImagePath;
    }

    public void setRemoteImagePath(String remoteImagePath) {
        this.remoteImagePath = remoteImagePath;
    }

    public String getRemoteMusicPath() {
        return remoteMusicPath;
    }

    public void setRemoteMusicPath(String remoteMusicPath) {
        this.remoteMusicPath = remoteMusicPath;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
