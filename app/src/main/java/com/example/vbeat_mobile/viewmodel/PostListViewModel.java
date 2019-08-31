package com.example.vbeat_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PostListViewModel extends ViewModel {
    // not live data because post list does not update automatically
    private List<PostViewModel> postList;

    public List<PostViewModel> getPostList() {
        return postList;
    }

    public void setPostList(List<PostViewModel> postList) {
        this.postList = postList;
    }
}
