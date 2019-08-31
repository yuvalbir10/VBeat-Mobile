package com.example.vbeat_mobile.backend.post.repository;

public class PostChangeData {
    private String postId;
    private String newDescription;
    private boolean isDeleted;

    public PostChangeData(
            String postId,
            String newDescription,
            boolean isDeleted
    ) {
        this.postId = postId;
        this.newDescription = newDescription;
        this.isDeleted = isDeleted;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public String getPostId() {
        return postId;
    }

    public boolean getIsDeleted(){
        return isDeleted;
    }
}
