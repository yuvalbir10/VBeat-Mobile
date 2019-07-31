package com.example.vbeat_mobile.backend.user;

import com.google.firebase.auth.FirebaseUser;

public class FirebaseUserAdapter implements User {
    private FirebaseUser user;

    public FirebaseUserAdapter(FirebaseUser user) {
        if(user == null) {
            throw new NullPointerException("can't have null user adapter");
        }

        this.user = user;
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getDisplayName() {
        return user.getDisplayName();
    }
}
