package com.example.vbeat_mobile.utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.example.vbeat_mobile.R;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

public class ImageViewUtil {
    // 50 MB of Cache
    private static int CACHE_SIZE = 50 * 1024 * 1024;

    private static class ImageViewUtilInstanceHolder {
        private static ImageViewUtil instance;
    }

    public static ImageViewUtil getInstance(){
        return ImageViewUtilInstanceHolder.instance;
    }

    public static void initialize(Context c){
        ImageViewUtilInstanceHolder.instance = new ImageViewUtil(c);
    }

    private ImageViewUtil(Context c){
        // build picasso with custom cache
        Picasso.Builder builder = new Picasso.Builder(c);
        Picasso p = builder.memoryCache(new LruCache(CACHE_SIZE)).build();
        Picasso.setSingletonInstance(p);
    }

    public void displayAndCache(ImageView imageView, String url){
        Picasso.get()
                .load(url)
//                .placeholder(R.drawable.progress_animation)
                .into(imageView);
    }

    public void displayAndCache(ImageView imageView, Uri uri) {
        Picasso.get()
                .load(uri)
//                .placeholder(R.drawable.progress_animation)
                .into(imageView);
    }
}
