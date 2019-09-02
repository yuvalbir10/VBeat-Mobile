package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.vbeat_mobile.backend.comment.repository.CommentRepository;

public class CommentViewModel extends ViewModel {
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

    public static boolean deleteComment(String commentId) {
        return CommentRepository.getInstance().deleteComment(commentId);
    }

    public static boolean comment(String postId, String commentText) {
        return CommentRepository.getInstance().comment(postId, commentText);
    }
}
