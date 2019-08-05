package com.example.vbeat_mobile.backend.post;

import android.media.Image;

import com.example.vbeat_mobile.backend.user.VBeatUser;

import java.util.HashMap;
import java.util.Map;

//TODO
public class FirebasePostAdapter implements VBeatPost {
    private String description;
    private String remoteImagePath;
    private String remoteMusicPath;
    private VBeatUser uploader;
    private String postId;

    public FirebasePostAdapter(
            String postId,
            String description,
            String remoteImagePath,
            String remoteMusicPath,
            VBeatUser uploader) {
        this.postId = postId;
        this.description = description;
        this.remoteImagePath = remoteImagePath;
        this.remoteMusicPath = remoteMusicPath;
        this.uploader = uploader;
    }

    @Override
    public Image getImage() {
        // download image here? maybe?
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public VBeatUser getUploader() {
        return null;
    }

    @Override
    public String getPostId() {
        return null;
    }

    public static Map<String, Object> toFirebaseMap(String description,
                                                    String remoteImagePath,
                                                    String remoteMusicPath,
                                                    VBeatUser uploader) {
        Map<String, Object> map = new HashMap<>();

        map.put("description", description);
        map.put("storage_image_path", remoteImagePath);
        map.put("storage_music_path", remoteMusicPath);
        map.put("uploader_id", uploader.getUserId());

        return map;
    }

    //TODO : add getMusicFile
}
