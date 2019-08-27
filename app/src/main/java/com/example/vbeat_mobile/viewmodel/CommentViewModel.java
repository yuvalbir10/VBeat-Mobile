package com.example.vbeat_mobile.viewmodel;

public class CommentViewModel {
    private String username;
    private String commentText;

    public CommentViewModel(
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
}
