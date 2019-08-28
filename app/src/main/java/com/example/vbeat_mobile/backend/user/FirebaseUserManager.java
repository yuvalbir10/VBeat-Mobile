package com.example.vbeat_mobile.backend.user;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static FirebaseUserManager getInstance(){
        return FirebaseUserManagerInstanceHolder.instance;
    }

    private FirebaseUserManager(){
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean isUserLoggedIn() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }

    @Override
    public void createAccount(String email, String password) throws UserRegistrationFailedException {
        if(isUserLoggedIn()) {
            throw new IllegalStateException("can't create account while user is logged in");
        }

        Task<AuthResult> t = mAuth.createUserWithEmailAndPassword(email, password);
        FirebaseFirestore instance = FirebaseFirestore.getInstance();


        // wait for result
        try {
            // register user with firebase authentication
            Tasks.await(t);

            if(t.getResult() == null) {
                Log.wtf(TAG, "await failed (?)");
                throw new ExecutionException(new RuntimeException("Tasks.await failed"));
            }

            VBeatUserModel userToBeRegistered = new VBeatUserModel(
                    email, getDisplayNameFromEmail(email),
                    t.getResult().getUser().getUid()
            );
            // write user details into users collection
            instance.collection(USERS_COLLECTION).document(userToBeRegistered.getUserId())
                    .update(createFirebaseFromModel(userToBeRegistered));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "register user has failed with exception", e);
        }
        // get exception if there was one
        Exception e = t.getException();

        if(!t.isSuccessful()) {
            if(e != null){
                throw new UserRegistrationFailedException(e.getMessage());
            } else {
                throw new UserRegistrationFailedException("unknown reason");
            }
        }
    }

    @Override
    public boolean deleteUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null) {
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
        if(isUserLoggedIn()) {
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
        if(!t.isSuccessful()) {
            Exception e = t.getException();
            if (e != null) {
                throw new UserLoginFailedException(e.getMessage());
            } else {
                throw new UserLoginFailedException("unknown reason");
            }
        }
    }

    public List<VBeatUserModel> getUsers(List<String> userIds) {
        throw new RuntimeException("not implemented");
    }

    public VBeatUserModel getUser(String userId){
        throw new RuntimeException("method not implemented");
    }

    private static Map<String, Object> createFirebaseFromModel(VBeatUserModel vBeatUserModel){
        Map<String, Object> obj = new HashMap<>();
        obj.put("display_name", vBeatUserModel.getDisplayName());
        obj.put("email", vBeatUserModel.getEmail());

        return obj;
    }

    private static String getDisplayNameFromEmail(String email) {
        return email.substring(0, email.indexOf('@'));
    }
}
