package com.example.vbeat_mobile.viewmodel;

public class CommentViewModel {
    private String username;
    private String commentText;
    private String userId;
    private String commentId;

    public CommentViewModel(
            String userId,
            String commentId,
            String username,
            String commentText
    ){
        this.username = username;
        this.commentText = commentText;
        this.userId = userId;
        this.commentId = commentId;
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
