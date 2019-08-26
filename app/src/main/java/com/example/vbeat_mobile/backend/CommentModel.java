package com.example.vbeat_mobile.backend;

public class CommentModel {
    private String userId;
    private String commentText;
    private String postId;

    public CommentModel(
            String userId,
            String commentText,
            String postId
    ){
        this.userId = userId;
        this.commentText = commentText;
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getPostId() {
        return postId;
    }
}
