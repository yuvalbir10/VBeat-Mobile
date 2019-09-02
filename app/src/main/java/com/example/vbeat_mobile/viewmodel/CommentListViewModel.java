package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.vbeat_mobile.backend.comment.repository.CommentRepository;

import java.util.List;

public class CommentListViewModel extends ViewModel {
    private LiveData<List<CommentViewModel>> commentViewModelLiveData;

    public LiveData<List<CommentViewModel>> getComments(String postId) {
        if(commentViewModelLiveData == null) {
            commentViewModelLiveData = CommentRepository.getInstance().listenOnLiveCommentChanges(postId);
        }
        return commentViewModelLiveData;
    }
}
