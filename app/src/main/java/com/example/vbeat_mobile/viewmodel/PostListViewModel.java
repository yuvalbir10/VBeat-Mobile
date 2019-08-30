package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PostListViewModel extends ViewModel {
    private LiveData<List<PostViewModel>> postListViewModel;

    public LiveData<List<PostViewModel>> getPostListViewModel() {
        return postListViewModel;
    }

    public void setPostListViewModel(LiveData<List<PostViewModel>> postListViewModel) {
        this.postListViewModel = postListViewModel;
    }
}
