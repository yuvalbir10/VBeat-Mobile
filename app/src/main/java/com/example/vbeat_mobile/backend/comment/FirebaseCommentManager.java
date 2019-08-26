package com.example.vbeat_mobile.backend.comment;

import android.util.Log;

import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.VBeatUserModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseCommentManager {
    private static final String TAG = "FirebaseCommentManager";
    private static final String COMMENT_COLLECTION = "comments";

    private static class FirebaseCommentManagerInstanceHolder {
        private static FirebaseCommentManager instance = new FirebaseCommentManager();
    }

    public static FirebaseCommentManager getInstance() {
        return FirebaseCommentManagerInstanceHolder.instance;
    }

    private FirebaseCommentManager() {
        Log.d(TAG, "FirebaseCommentManager instance created");
    }

    public List<CommentModel> getComments(String postId) throws CommentException {
        List<CommentModel> commentModelLinkedList = new LinkedList<>();
        FirebaseFirestore instance = FirebaseFirestore.getInstance();

        QuerySnapshot qs = null;
        try {
            qs = Tasks.await(
                    instance.collection(COMMENT_COLLECTION).whereEqualTo("postId", postId).get()
            );
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "unable to fetch comments for post", e);
            throw new CommentException(e.getMessage());
        }

        for(DocumentSnapshot ds : qs.getDocuments()) {
            commentModelLinkedList.add(documentToCommentModel(ds));
        }

        return commentModelLinkedList;
    }

    public CommentModel comment(String commentText, String postId) throws CommentException {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        FirebaseUserManager firebaseUserManager = FirebaseUserManager.getInstance();
        VBeatUserModel currentUser = firebaseUserManager.getCurrentUser();

        if (currentUser == null) {
            Log.w(TAG, "currentUser == null on FirebaseCommentManager");
            throw new CommentException("unable to post comment because user is not logged in");
        }

        try {
            DocumentReference docRef = Tasks.await(instance.collection(COMMENT_COLLECTION).add(
                    createFirebaseCommentFromParams(
                            commentText, postId, currentUser.getUserId()
                    )));

            return new CommentModel(docRef.getId(), currentUser.getUserId(), commentText, postId);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Unable to add comment", e);
            throw new CommentException(e.getMessage());
        }
    }

    public void deleteComment(String commentId) throws CommentException {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();

        try {
            Void v = Tasks.await(
                    instance.collection(COMMENT_COLLECTION).document(commentId).delete()
            );
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "unable to delete comment", e);
            throw new CommentException(e.getMessage());
        }

    }

    // this method assumes the comment exists
    public void editComment(String commentId, String commentText) throws CommentException {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();

        try {
            Tasks.await(
                    instance.collection(COMMENT_COLLECTION).document(commentId)
                            .update(
                                    "commentText", commentText
                            )
            );
        } catch(ExecutionException | InterruptedException e) {
            Log.e(TAG, "unable to edit comment", e);
            throw new CommentException(e.getMessage());
        }

    }

    private Map<String, Object> createFirebaseCommentFromParams(String commentText,
                                                                String postId,
                                                                String userId) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("commentText", commentText);
        obj.put("postId", postId);
        obj.put("userId", userId);
        return obj;
    }

    private CommentModel documentToCommentModel(DocumentSnapshot ds) {
        return new CommentModel(
                ds.getId(),
                (String)ds.get("userId"),
                (String)ds.get("commentText"),
                (String)ds.get("postId")
        );
    }
}
