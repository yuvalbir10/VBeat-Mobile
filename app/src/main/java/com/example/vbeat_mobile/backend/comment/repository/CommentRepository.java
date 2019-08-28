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
import com.example.vbeat_mobile.viewmodel.CommentViewModel;
import com.google.firebase.auth.FirebaseUser;

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
                    liveData.setValue(commentViewModels);
                } catch (CommentException e) {
                    Log.e(TAG, "unable to grab comments", e);
                    liveData.setValue(null);
                }
            }
        }).start();


        return liveData;
    }

    private List<CommentViewModel> convertCommentModelsToViewModels(List<CommentModel> commentModels) throws CommentException {
        List<String> userIds = new LinkedList<>();
        List<CommentViewModel> commentViewModels = new LinkedList<>();

        for (CommentModel cm : commentModels) {
            userIds.add(cm.getUserId());
        }

        FirebaseUserManager userManager = FirebaseUserManager.getInstance();
        List<VBeatUserModel> userModels = null;
        try {
            userModels = userManager.getUsers(userIds);
        } catch (UserBackendException e) {
            throw new CommentException("unable to grab users for comments");
        }

        // match usernames to user ids
        for (CommentModel commentModel : commentModels) {
            for (VBeatUserModel user : userModels) {
                if (user.getUserId().equals(commentModel.getUserId())) {
                    commentViewModels.add(new CommentViewModel(
                            user.getUserId(),
                            commentModel.getPostId(),
                            user.getDisplayName(),
                            commentModel.getCommentText()
                    ));
                }
            }
        }

        return commentViewModels;
    }
}
