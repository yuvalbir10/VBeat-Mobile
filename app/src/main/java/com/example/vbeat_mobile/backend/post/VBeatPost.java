package com.example.vbeat_mobile.backend.post;

import android.media.Image;

import com.example.vbeat_mobile.backend.user.VBeatUser;

public interface VBeatPost {
    Image getImage();

    String getDescription();

    VBeatUser getUploader();

    String getPostId();
    //TODO : add getMusicFile function

}
