package com.example.vbeat_mobile.UI;

import com.example.vbeat_mobile.viewmodel.PostViewModel;

import java.util.Comparator;

public class PostViewModelDateComparator implements Comparator<PostViewModel> {
    @Override
    public int compare(PostViewModel postViewModel, PostViewModel t1) {
        if(postViewModel.getUploadDate().before(t1.getUploadDate())) {
            return 1;
        } else {
            return -1;
        }
    }
}
