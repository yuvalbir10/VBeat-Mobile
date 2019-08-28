package com.example.vbeat_mobile.viewmodel;

public class CommentViewModel {
    private String username;
    private String commentText;
    private String userId;
    private String commentId;

    public CommentViewModel(
            String userId,
            String postId,
            String username,
            String commentText
    ){
        this.username = username;
        this.commentText = commentText;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId(){
        return userId;
    }

    public String getCommentId(){
        return commentId;
    }
}
