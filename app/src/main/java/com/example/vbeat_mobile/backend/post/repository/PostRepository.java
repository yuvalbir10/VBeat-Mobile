package com.example.vbeat_mobile.backend.post.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vbeat_mobile.viewmodel.PostViewModel;
import com.example.vbeat_mobile.backend.post.FirebasePostManager;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;

public class PostRepository {
    private PostCache postCache = null;

    private static class PostRepositoryInstanceHolder {
        private static PostRepository instance = new PostRepository();
    }

    public static PostRepository getInstance(){
        return PostRepositoryInstanceHolder.instance;
    }

    private PostRepository(){
        postCache = new PostCache();
    }

    public LiveData<PostViewModel> getPost(final String postId) {
        final MutableLiveData<PostViewModel> resPost = new MutableLiveData<>();
        VBeatPostModel cachedPost = postCache.getPost(postId);
        if(cachedPost != null) {
            resPost.setValue(
                    getViewModelFromModel(cachedPost)
            );
            return resPost;
        }

        // run in background
        new Thread(new Runnable() {
            @Override
            public void run() {
                VBeatPostModel fetchedPost = FirebasePostManager.getInstance().getPost(postId);
                postCache.savePost(fetchedPost);

                resPost.setValue(
                        getViewModelFromModel(fetchedPost)
                );
            }
        }).start();

        return resPost;
    }

    private PostViewModel getViewModelFromModel(VBeatPostModel model){
        return new PostViewModel(
                model.getPostId(),
                model.getDescription(),
                model.getRemoteImagePath(),
                model.getRemoteMusicPath(),
                model.getUploaderId()
        );
    }
}
