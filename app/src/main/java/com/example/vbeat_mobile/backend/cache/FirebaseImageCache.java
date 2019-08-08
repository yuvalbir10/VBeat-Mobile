package com.example.vbeat_mobile.backend.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.ExecutionException;

public class FirebaseImageCache implements Cache<Bitmap, String> {
    private static final String TAG = "FirebaseImageCache";

    // 100 MBs max download size
    private static final long maxDownloadSize = 100 * 1024 * 1024;

    private static class FirebaseImageCacheInstanceHolder {
        private static FirebaseImageCache instance = new FirebaseImageCache();
    }

    public static FirebaseImageCache getInstance(){
        return FirebaseImageCacheInstanceHolder.instance;
    }

    @Override
    public Bitmap get(String key) throws CacheFailException {
        // if image is in cache we'll load it from cache
        if(inCache(key)) {
            return loadFromCache(key);
        }

        // download image from server;
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = ref.child(key);

        byte[] imageBytes = null;
        try {
            imageBytes = Tasks.await(imageRef.getBytes(maxDownloadSize));
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "imageRef.getBytes was interrupted", e);
            throw new CacheFailException("getBytes was interrupted");
        }
        // get bitmap and save to cache
        Bitmap resBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        saveToCache(resBitmap, key);

        return resBitmap;
    }

    private void saveToCache(Bitmap b, String key){

    }

    private Bitmap loadFromCache(String key){
        return null;
    }

    private boolean inCache(String key){
        Log.w(TAG, "inCache always returns false");
        return false;
    }
}
