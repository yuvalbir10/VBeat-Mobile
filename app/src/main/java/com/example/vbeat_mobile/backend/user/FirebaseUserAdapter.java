package com.example.vbeat_mobile.backend.user;

import com.google.firebase.auth.FirebaseUser;

public class FirebaseUserAdapter extends VBeatUserModel {

    public FirebaseUserAdapter(FirebaseUser user) {
        super(user.getEmail(), user.getDisplayName(), user.getUid());
    }
}
