package com.example.vbeat_mobile.backend.post;

import android.media.Image;

import com.example.vbeat_mobile.backend.user.VBeatUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirebasePostAdapter implements VBeatPost {
    private String description;
    private String remoteImagePath;
    private String remoteMusicPath;
    private VBeatUser uploader;
    private String uploaderId;
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

    public FirebasePostAdapter(DocumentSnapshot ds){
        this.postId = ds.getId();
        this.description = (String)ds.get("description");
        this.remoteImagePath = (String)ds.get("storage_image_path");
        this.remoteMusicPath = (String)ds.get("storage_music_path");
        this.uploader = null;
        this.uploaderId = (String)ds.get("uploader_id");
    }

    @Override
    public Image getImage() {
        // download image here? maybe?
        return null;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public VBeatUser getUploader() {
        if(uploader == null){
            // download uploader
        }

        return uploader;
    }

    @Override
    public String getPostId() {
        return postId;
    }

    @Override
    public byte[] getMusicFile() {
        // lazy get music file

        return new byte[0];
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


}
