package com.example.vbeat_mobile.backend.post;

import android.media.Image;

import com.example.vbeat_mobile.backend.user.VBeatUser;

public interface VBeatPost {
    Image getImage();
    String getDescription();
    VBeatUser getUploader();
    //TODO : add getMusicFile function

}
