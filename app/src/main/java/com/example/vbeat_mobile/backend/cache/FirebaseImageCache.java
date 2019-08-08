package com.example.vbeat_mobile.backend.cache;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.util.Log;

import com.example.vbeat_mobile.UI.MainActivity;
import com.example.vbeat_mobile.utility.URIUtils;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

/*
* Cache mechanism:
* For each new image, the key is the url to download it from firebase
* Each image will be saved in cache_image folder which will be
* created if not it does already exist
* an image filename will be the base64 of the key
* */
public class FirebaseImageCache implements Cache<Bitmap, String> {
    private static final String TAG = "FirebaseImageCache";
    private Application application;
    private boolean saveImages = false;

    // 100 MBs max download size
    private static final long maxDownloadSize = 100 * 1024 * 1024;

    private static class FirebaseImageCacheInstanceHolder {
        private static FirebaseImageCache instance = new FirebaseImageCache();
    }

    public static void setApplicationContext(Application app) {
        getInstance().application = app;
    }

    public static void setSaveImages(boolean saveImages){
        getInstance().saveImages = saveImages;
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

        // currently disabled due to complexity
        if(saveImages){
            try {
                saveToCache(resBitmap, key);
            } catch (IOException e) {
                Log.e(TAG, "saveToCache failed", e);
            }
        }

        return resBitmap;
    }

    private void saveToCache(Bitmap b, String key) throws IOException {
        String filename = Base64.encodeToString(
                key.getBytes(StandardCharsets.UTF_8),
                Base64.DEFAULT
        );

        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(getCacheDir(), filename));
            b.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch(IOException e){
            throw e;
        } finally {
            if(os != null) {
                os.flush();
                os.close();
            }
        }
    }

    private String getCacheDir(){
        return URIUtils.getDataDir(application) + "/firebase_image_cache/";
    }

    // not implemented
    // currently disabling saving images
    private Bitmap loadFromCache(String key){
        return null;
    }

    private boolean inCache(String key){
        Log.w(TAG, "inCache always returns false");
        return false;
    }
}
