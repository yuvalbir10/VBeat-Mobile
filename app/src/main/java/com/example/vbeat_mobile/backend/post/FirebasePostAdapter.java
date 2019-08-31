package com.example.vbeat_mobile.backend.post;

import com.example.vbeat_mobile.backend.user.VBeatUserModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class FirebasePostAdapter extends VBeatPostModel {
    public FirebasePostAdapter(
            String postId,
            String description,
            String remoteImagePath,
            String remoteMusicPath,
            String uploader) {
        this.postId = postId;
        this.description = description;
        this.remoteImagePath = remoteImagePath;
        this.remoteMusicPath = remoteMusicPath;
        this.uploaderId = uploader;
    }

    public FirebasePostAdapter(DocumentSnapshot ds){
        this.postId = ds.getId();
        this.description = (String)ds.get("description");
        this.remoteImagePath = (String)ds.get("storage_image_path");
        this.remoteMusicPath = (String)ds.get("storage_music_path");
        this.uploaderId = (String)ds.get("uploader_id");
    }

    public static Map<String, Object> toFirebaseMap(String description,
                                                    String remoteImagePath,
                                                    String remoteMusicPath,
                                                    VBeatUserModel uploader) {
        Map<String, Object> map = new HashMap<>();

        map.put("description", description);
        map.put("storage_image_path", remoteImagePath);
        map.put("storage_music_path", remoteMusicPath);
        map.put("uploader_id", uploader.getUserId());
        map.put("timestamp", FieldValue.serverTimestamp());

        return map;
    }
}
