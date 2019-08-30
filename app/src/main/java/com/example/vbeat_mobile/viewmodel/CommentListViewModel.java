package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CommentListViewModel extends ViewModel {
    private LiveData<List<CommentViewModel>> commentViewModelLiveData;

    public LiveData<List<CommentViewModel>> getComments() {
        return commentViewModelLiveData;
    }

    public void setCommentViewModelLiveData(LiveData<List<CommentViewModel>> commentViewModelLiveData) {
        this.commentViewModelLiveData = commentViewModelLiveData;
    }
}
