package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.vbeat_mobile.backend.post.repository.PostChangeData;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.google.firebase.firestore.ListenerRegistration;

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

    public void setDescription(String other){ description = other; }

    public static LiveData<PostViewModel> getPost(String postId) {
        return PostRepository.getInstance().getPost(postId);
    }

    public static boolean editPost(String postId, String description) {
        return PostRepository.getInstance().editPost(postId, description);
    }

    public static LiveData<PostChangeData> listenToPostChange(String postId) {
        return PostRepository.getInstance().listenToPostChange(postId);
    }

    public static void updatePostInCache(String postId, String description) {
        PostRepository.getInstance().getPostCache()
                .updatePost(postId, description);
    }

    public static ListenerRegistration listenToNewPost(Runnable r, String firstPostId){
        return PostRepository.getInstance().listenToNewPost(r, firstPostId);
    }

    public static boolean deletePost(String postId) {
        return PostRepository.getInstance().deletePost(postId);
    }
}
