package com.example.vbeat_mobile.backend.comment;

public class CommentModel {
    private String commentId;
    private String userId;
    private String commentText;
    private String postId;

    public CommentModel(
            String commentId,
            String userId,
            String commentText,
            String postId
    ){
        this.commentId = commentId;
        this.userId = userId;
        this.commentText = commentText;
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
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
