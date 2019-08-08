package com.example.vbeat_mobile.backend.post;

import android.graphics.Bitmap;
import android.media.Image;

import com.example.vbeat_mobile.backend.user.VBeatUser;

public interface VBeatPost {
    Bitmap getImage();

    String getDescription();

    VBeatUser getUploader();

    String getPostId();

    byte[] getMusicFile();
}
