package com.example.vbeat_mobile.backend.post.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vbeat_mobile.backend.comment.CommentException;
import com.example.vbeat_mobile.backend.post.DeletePostException;
import com.example.vbeat_mobile.backend.post.FirebasePostManager;
import com.example.vbeat_mobile.backend.post.UploadPostFailedException;
import com.example.vbeat_mobile.backend.post.VBeatPostCollection;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.utility.ListenerRemoverLiveData;
import com.example.vbeat_mobile.viewmodel.PostViewModel;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PostRepository {
    private static final String TAG = "PostRepository";
    private PostCache postCache = null;

    private static class PostRepositoryInstanceHolder {
        private static PostRepository instance = new PostRepository();
    }

    public static PostRepository getInstance() {
        return PostRepositoryInstanceHolder.instance;
    }

    private PostRepository() {
        postCache = new PostCache();
    }

    public LiveData<PostViewModel> getPost(final String postId) {
        final MutableLiveData<PostViewModel> resPost = new MutableLiveData<>();


        // run in background
        new Thread(new Runnable() {
            @Override
            public void run() {
                VBeatPostModel cachedPost = postCache.getPost(postId);
                if (cachedPost == null) {
                    cachedPost = FirebasePostManager.getInstance().getPost(postId);
                    postCache.savePost(cachedPost);
                }


                resPost.postValue(
                        getViewModelFromModel(cachedPost)
                );
            }
        }).start();

        return resPost;
    }

    public boolean editPost(String postId, String description) {
        try {
            FirebasePostManager.getInstance().editPost(postId, description);

            // update post cache
            VBeatPostModel postModel = postCache.getPost(postId);
            postModel.setDescription(description);
            postCache.savePost(postModel);
            return true;
        } catch (UploadPostFailedException e) {
            Log.e(TAG, "unable to edit post", e);
            return false;
        }

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

                if (postCollection != null) {
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


    public LiveData<PostChangeData> listenToPostChange(final String postId) {
        return new ListenerRemoverLiveData<>(new ListenerRemoverLiveData.FirebaseListenerCreator<PostChangeData>() {
            @Override
            public ListenerRegistration createListener(final MutableLiveData<PostChangeData> liveData) {
                return FirebasePostManager.getInstance().listenToPostChanges(postId,
                        new FirebasePostManager.PostChangesListener() {
                            @Override
                            public void onPostChanged(String postId1,
                                                      String newDescription,
                                                      boolean isDeleted) {
                                liveData.postValue(
                                        new PostChangeData(
                                                postId1,
                                                newDescription,
                                                isDeleted
                                        )
                                );
                            }
                        });
            }
        });
    }

    // cleaned by caller
    public ListenerRegistration listenToNewPost(final Runnable runOnNewPost, String firstPostId) {
        return FirebasePostManager.getInstance().listenToNewPost(new FirebasePostManager.NewPostListener() {
            @Override
            public void onNewPost() {
                runOnNewPost.run();
            }
        }, firstPostId);
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

    public boolean deletePost(String postId) {
        try {
            FirebasePostManager.getInstance().deletePost(postId);
            return true;
        } catch (DeletePostException | CommentException e) {
            Log.e(TAG, "delete post failed", e);
            return false;
        }
    }

    public VBeatPostModel uploadPost(String description, Uri imageUri, Uri musicUri) {
        try {
            return FirebasePostManager.getInstance().uploadPost(description, imageUri, musicUri);
        } catch (UploadPostFailedException e) {
            Log.e(TAG, "delete post failed", e);
            return null;
        }
    }

    public PostCache getPostCache() {
        return postCache;
    }

    private PostViewModel getViewModelFromModel(VBeatPostModel model) {
        return new PostViewModel(
                model.getPostId(),
                model.getDescription(),
                model.getRemoteImagePath(),
                model.getRemoteMusicPath(),
                model.getUploaderId(),
                model.getUploadTime().toDate()
        );
    }

    private void modelListToViewModelList(List postList, List<PostViewModel> postViewModelList) {
        if (postList == null) {
            Log.e(TAG, "modeListToViewModelList: postList == null");
            return;
        }

        for (Object post : postList) {
            if (!(post instanceof VBeatPostModel)) {
                Log.wtf(TAG, "post was not of type VBeatPostModel");
                throw new RuntimeException("Something really weird happened");
            }

            VBeatPostModel concretePost = (VBeatPostModel) post;
            postViewModelList.add(
                    getViewModelFromModel(concretePost)
            );
        }
    }
}
