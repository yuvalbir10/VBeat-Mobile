package com.example.vbeat_mobile.backend;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vbeat_mobile.backend.user.FirebaseUserAdapter;
import com.example.vbeat_mobile.backend.user.UserRegistrationFailedException;
import com.example.vbeat_mobile.backend.user.VBeatUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FirebaseUserManager implements UserManager {
    private static final String TAG = "FirebaseUserManager";

    private FirebaseAuth mAuth;

    public FirebaseUserManager(){
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

        // wait for result
        try {
            Tasks.await(t);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "Tasks.await was interrupted", e);
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
        userDeletionTask.getResult();

        // check to see if we were able to delete the account
        return userDeletionTask.isSuccessful();
    }

    @Override
    public VBeatUser getCurrentUser() {
        return new FirebaseUserAdapter(mAuth.getCurrentUser());
    }
}
