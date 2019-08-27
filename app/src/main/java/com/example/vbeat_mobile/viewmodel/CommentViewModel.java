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

    public String getUserId(){
        //TODO: ishay to implement
        return null;
    }

    public String getCommentId(){
        //TODO: ishay to implement
        return null;
    }
}
