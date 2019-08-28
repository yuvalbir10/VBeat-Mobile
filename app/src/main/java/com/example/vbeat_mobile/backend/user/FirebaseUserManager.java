package com.example.vbeat_mobile.backend.user;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/*
 * This can be a singleton
 * But I don't think it makes much difference if it isn't
 * */
public class FirebaseUserManager implements UserManager {
    private static final String TAG = "FirebaseUserManager";
    private static final String USERS_COLLECTION = "vbeat_users";

    private FirebaseAuth mAuth;

    private static class FirebaseUserManagerInstanceHolder {
        private static FirebaseUserManager instance = new FirebaseUserManager();
    }

    public static FirebaseUserManager getInstance() {
        return FirebaseUserManagerInstanceHolder.instance;
    }

    private FirebaseUserManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean isUserLoggedIn() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }

    @Override
    public void createAccount(String email, String password) throws UserRegistrationFailedException {
        if (isUserLoggedIn()) {
            throw new IllegalStateException("can't create account while user is logged in");
        }

        Task<AuthResult> t = mAuth.createUserWithEmailAndPassword(email, password);
        FirebaseFirestore instance = FirebaseFirestore.getInstance();


        // wait for result
        try {
            // register user with firebase authentication
            Tasks.await(t);

            verifyResult(t);

            VBeatUserModel userToBeRegistered = new VBeatUserModel(
                    email, getDisplayNameFromEmail(email),
                    Objects.requireNonNull(t.getResult()).getUser().getUid()
            );
            // write user details into users collection
            Tasks.await(
                    instance.collection(USERS_COLLECTION).document(userToBeRegistered.getUserId())
                            .update(createFirebaseFromModel(userToBeRegistered))
            );
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "register user has failed with exception", e);
        }
        // get exception if there was one
        Exception e = t.getException();

        if (!t.isSuccessful()) {
            if (e != null) {
                throw new UserRegistrationFailedException(e.getMessage());
            } else {
                throw new UserRegistrationFailedException("unknown reason");
            }
        }
    }

    @Override
    public boolean deleteUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            throw new IllegalStateException("you have to login to delete");
        }


        // wait for task to finish
        Task<Void> userDeletionTask = user.delete();
        try {
            Tasks.await(userDeletionTask);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.d(TAG, "Tasks.await was interrupted", e);
        }

        // check to see if we were able to delete the account
        return userDeletionTask.isSuccessful();
    }

    @Override
    public VBeatUserModel getCurrentUser() {
        return new FirebaseUserAdapter(mAuth.getCurrentUser());
    }

    @Override
    public void login(String email, String password) throws UserLoginFailedException {
        if (isUserLoggedIn()) {
            throw new UserLoginFailedException("can't login while user is already logged in");
        }

        Task<AuthResult> t = mAuth.signInWithEmailAndPassword(email, password);


        // wait for task to complete
        try {
            Tasks.await(t);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "login Tasks.await was interrupted", e);
        }

        // check if we managed to login or not
        if (!t.isSuccessful()) {
            Exception e = t.getException();
            if (e != null) {
                throw new UserLoginFailedException(e.getMessage());
            } else {
                throw new UserLoginFailedException("unknown reason");
            }
        }
    }

    public List<VBeatUserModel> getUsers(List<String> userIds) throws UserBackendException {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        List<VBeatUserModel> userModels = new LinkedList<>();

        Task<QuerySnapshot> t = instance.collection(USERS_COLLECTION).get();

        try {
            verifyResult(t);
            Tasks.await(t);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "getUsers failed with exception", e);
            throw new UserBackendException(e.getMessage());
        }

        List<DocumentSnapshot> dsList = Objects.requireNonNull(t.getResult()).getDocuments();

        for (DocumentSnapshot ds : dsList) {
            VBeatUserModel userModel = getModelFromFirebase(ds);
            if (userIds.contains(userModel.getUserId())) {
                userModels.add(userModel);
            }
        }

        return userModels;
    }

    public VBeatUserModel getUser(String userId) throws UserBackendException {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();

        Task<DocumentSnapshot> t = instance.collection(USERS_COLLECTION).document(userId).get();
        try {
            Tasks.await(t);
            verifyResult(t);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "getUser failed with exception", e);
            throw new UserBackendException(e.getMessage());
        }

        DocumentSnapshot ds = t.getResult();
        if(ds == null) {
            throw new UserBackendException("unable to find user");
        }

        return getModelFromFirebase(ds);
    }

    private static Map<String, Object> createFirebaseFromModel(VBeatUserModel vBeatUserModel) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("display_name", vBeatUserModel.getDisplayName());
        obj.put("email", vBeatUserModel.getEmail());

        return obj;
    }

    private static VBeatUserModel getModelFromFirebase(DocumentSnapshot ds) {
        return new VBeatUserModel(
                (String) ds.get("email"),
                (String) ds.get("display_name"),
                ds.getId()
        );
    }

    private static String getDisplayNameFromEmail(String email) {
        return email.substring(0, email.indexOf('@'));
    }

    private static void verifyResult(Task t) throws ExecutionException {
        if (t.getResult() == null) {
            Log.wtf(TAG, "verifyResult failed");
            throw new ExecutionException(new RuntimeException("failed verify result check"));
        }
    }
}
