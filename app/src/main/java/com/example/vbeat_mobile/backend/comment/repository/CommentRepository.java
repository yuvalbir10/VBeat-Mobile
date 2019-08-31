package com.example.vbeat_mobile.backend.comment.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vbeat_mobile.backend.comment.CommentException;
import com.example.vbeat_mobile.backend.comment.CommentModel;
import com.example.vbeat_mobile.backend.comment.FirebaseCommentManager;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.UserBackendException;
import com.example.vbeat_mobile.backend.user.VBeatUserModel;
import com.example.vbeat_mobile.backend.user.repository.UserRepository;
import com.example.vbeat_mobile.viewmodel.CommentViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.LinkedList;
import java.util.List;

public class CommentRepository {
    private static final String TAG = "CommentRepo";

    private static class CommentRepositoryInstanceHolder {
        private static CommentRepository instance = new CommentRepository();
    }

    public static CommentRepository getInstance() {
        return CommentRepositoryInstanceHolder.instance;
    }

    private CommentRepository() {

    }

    public LiveData<List<CommentViewModel>> getComments(final String postId) {
        final MutableLiveData<List<CommentViewModel>> liveData = new MutableLiveData<>();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseCommentManager instance = FirebaseCommentManager.getInstance();
                    List<CommentModel> commentModel = instance.getComments(postId);
                    List<CommentViewModel> commentViewModels = convertCommentModelsToViewModels(commentModel);
                    liveData.postValue(commentViewModels);
                } catch (CommentException e) {
                    Log.e(TAG, "unable to grab comments", e);
                    liveData.postValue(null);
                }
            }
        }).start();


        return liveData;
    }

    public LiveData<List<CommentViewModel>> listenOnLiveCommentChanges(String postId) {
        final MutableLiveData<List<CommentViewModel>> commentListMutableLiveData = new MutableLiveData<>();


        ListenerRegistration listenerRegistration = FirebaseCommentManager.getInstance().listenOnCommentPageChanges(postId,
                new FirebaseCommentManager.CommentPageChangesListener() {
                    @Override
                    public void onCommentListChanged(List<CommentModel> newCommentList) {

                        try {
                            List<CommentViewModel> commentViewModelList =
                                    convertCommentModelsToViewModels(newCommentList);
                            commentListMutableLiveData.postValue(commentViewModelList);
                        } catch (CommentException e) {
                            Log.e(TAG, "Comment update failed", e);
                        }
                    }
                });

        return commentListMutableLiveData;
    }

    private List<CommentViewModel> convertCommentModelsToViewModels(List<CommentModel> commentModels) throws CommentException {
        List<String> userIds = new LinkedList<>();
        List<CommentViewModel> commentViewModels = new LinkedList<>();

        for (CommentModel cm : commentModels) {
            userIds.add(cm.getUserId());
        }

        FirebaseUserManager userManager = FirebaseUserManager.getInstance();
        UserRepository userRepository = UserRepository.getInstance();
        List<VBeatUserModel> userModels = null;
        try {
            userModels = userManager.getUsers(userIds);
            // the reason for using the method from UserRepository
            // is because we don't want to do this operation async
            // we want to wait for it to be over
            userRepository.saveUsers(userModels);
        } catch (UserBackendException e) {
            throw new CommentException("unable to grab users for comments");
        }

        // match usernames to user ids
        for (CommentModel commentModel : commentModels) {
            for (VBeatUserModel user : userModels) {
                if (user.getUserId().equals(commentModel.getUserId())) {
                    commentViewModels.add(new CommentViewModel(
                            user.getUserId(),
                            commentModel.getCommentId(),
                            user.getDisplayName(),
                            commentModel.getCommentText()
                    ));
                }
            }
        }

        return commentViewModels;
    }
}
