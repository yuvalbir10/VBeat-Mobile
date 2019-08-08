package com.example.vbeat_mobile.backend.cache;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

public class FirebaseImageCache implements Cache<Bitmap, String> {
    private static final String TAG = "FirebaseImageCache";

    private static class FirebaseImageCacheInstanceHolder {
        private static FirebaseImageCache instance = new FirebaseImageCache();
    }

    public static FirebaseImageCache getInstance(){
        return FirebaseImageCacheInstanceHolder.instance;
    }

    @Override
    public Bitmap get(String key) {
        // if image is in cache we'll load it from cache
        if(inCache(key)) {
            return loadFromCache(key);
        }

        // download image from server;
    }

    private Bitmap loadFromCache(String key){
        return null;
    }

    private boolean inCache(String key){
        Log.w(TAG, "inCache always returns false");
        return false;
    }
}
