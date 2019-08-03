package com.example.vbeat_mobile.backend;

import androidx.annotation.NonNull;

import com.example.vbeat_mobile.backend.user.FirebaseUserAdapter;
import com.example.vbeat_mobile.backend.user.VBeatUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FirebaseUserManager implements UserManager {
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
    public boolean createAccount(String email, String password) {
        if(isUserLoggedIn()) {
            throw new IllegalStateException("can't create account while user is logged in");
        }

        // we will lock here
        // and then unlock when it's done
        // so this method is synchronous
        final Lock lock = new ReentrantLock();
        lock.lock();

        // trick to pass arguments
        // to the onComplete anonymous class
        final Boolean[] res = {false};


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                res[0] = task.isSuccessful();
                lock.unlock();
            }
        });

        // wait for the task to finish
        // unlock when we're done
        lock.lock();
        lock.unlock();

        // return whether or not the task was successful
        return res[0];
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
