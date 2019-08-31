package com.example.vbeat_mobile.backend.post.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vbeat_mobile.backend.post.FirebasePostManager;
import com.example.vbeat_mobile.backend.post.VBeatPostCollection;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.viewmodel.PostListViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PostRepository {
    private static final String TAG = "PostRepository";
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


        // run in background
        new Thread(new Runnable() {
            @Override
            public void run() {
                VBeatPostModel cachedPost = postCache.getPost(postId);
                if(cachedPost == null) {
                    cachedPost = FirebasePostManager.getInstance().getPost(postId);
                    postCache.savePost(cachedPost);
                }


                resPost.setValue(
                        getViewModelFromModel(cachedPost)
                );
            }
        }).start();

        return resPost;
    }

    public LiveData<List<PostViewModel>> getPosts(final String cursor, final int limit) {
        final MutableLiveData<List<PostViewModel>> resPost = new MutableLiveData<>();
        resPost.setValue(new ArrayList<PostViewModel>());

        new Thread(new Runnable() {
            @Override
            public void run() {
                VBeatPostCollection postCollection = FirebasePostManager
                        .getInstance().getPosts(cursor, limit);



                // android studio complaining
                // about not declaring type
                List postList = null;

                if(postCollection != null) {
                    // load from remote source if we can
                    postList = postCollection.getPosts();
                } else {
                    // load any posts we have and display them in feed
                    postList = postCache.getAllPostsInCache();
                    // take as much posts as you can but not more than
                    // limit - 1
                    postList = postList.subList(0, Math.min(limit - 1, postList.size()));
                }

                List<PostViewModel> postViewModelList = new LinkedList<>();
                modelListToViewModelList(postList, postViewModelList);

                resPost.postValue(postViewModelList);
            }
        }).start();

        return resPost;
    }




    public LiveData<List<PostViewModel>> getPostsByUser(final String userId) {
        final MutableLiveData<List<PostViewModel>> postsLiveData = new MutableLiveData<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<VBeatPostModel> postModels = FirebasePostManager.getInstance().getUserPosts(userId);
                List<PostViewModel> viewModelList = new LinkedList<>();
                modelListToViewModelList(postModels, viewModelList);

                // if the get user post fails
                // this posts null
                // so it's a "transparent" error handling
                postsLiveData.postValue(viewModelList);
            }
        }).start();

        return postsLiveData;
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

    private void modelListToViewModelList(List postList, List<PostViewModel> postViewModelList) {
        if(postList == null) {
            Log.e(TAG, "modeListToViewModelList: postList == null");
            return;
        }

        for(Object post : postList) {
            if(!(post instanceof VBeatPostModel)) {
                Log.wtf(TAG, "post was not of type VBeatPostModel");
                throw new RuntimeException("Something really weird happened");
            }

            VBeatPostModel concretePost = (VBeatPostModel)post;
            postViewModelList.add(
                    getViewModelFromModel(concretePost)
            );
        }
    }
}
