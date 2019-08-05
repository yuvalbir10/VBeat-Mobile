package com.example.vbeat_mobile.UI;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.post.FirebasePostManager;
import com.example.vbeat_mobile.backend.post.PostManager;
import com.example.vbeat_mobile.utility.ExifUtil;
import com.example.vbeat_mobile.utility.URIUtils;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadPostFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1, PICK_MUSIC_REQUEST = 2;
    private static final String TAG = "UploadPostFragment";

    private ImageView imageView;
    private Button chooseImageButton, chooseMusicButton;
    private Uri imageUri, musicUri;
    private PostManager<String> postManager;

    public UploadPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload_post, container, false);
        imageView = v.findViewById(R.id.imageView);
        chooseImageButton = v.findViewById(R.id.choose_image_button);
        chooseMusicButton = v.findViewById(R.id.choose_music_button);

        postManager = new FirebasePostManager();

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
            }
        });

        chooseMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMusicFromGallery();
            }
        });

        Button postImageButton = v.findViewById(R.id.post_button);

        postImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                postImage();
            }
        });

        return v;
    }

    private void postImage(){
        // get description & verify
        // verify music uri & image uri
        // run upload post in background while showing
        // loading animation using a progress bar or something
        String description = null;

//        postManager.uploadPost(description, imageUri, musicUri);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void pickMusicFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_MUSIC_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // can't continue if data is null
        // or if we have no context
        if (data == null || getContext() == null) {
            Log.e(TAG, "data || getContext() were null");
            return;
        }

        View v = getView();
        if (v == null) {
            Log.e(TAG, "view was null");
            return;
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imageView.setVisibility(View.VISIBLE);

            String path = URIUtils.getPath(getContext(), imageUri);

            // if we're getting a good path
            // load into image view
            if (path != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageUri = Uri.fromFile(new File(path));

                // prevent memory issues
                Bitmap scaled = Bitmap.createScaledBitmap(
                        bitmap,
                        512,
                        512,
                        true);

                Bitmap fixed = ExifUtil.rotateBitmap(path, scaled);

                ImageView image = v.findViewById(R.id.imageView);

                image.setImageBitmap(fixed);
                setTextViewFilename(v, R.id.imagePathTextView, path);
            }
        }

        if (requestCode == PICK_MUSIC_REQUEST && resultCode == RESULT_OK) {
            // content uri
            musicUri = data.getData();

            String musicPath = URIUtils.getPath(getContext(), musicUri);

            if (musicPath == null) {
                Log.e(TAG, "musicPath == null");
                return;
            }

            musicUri = Uri.fromFile(new File(musicPath));
            setTextViewFilename(v, R.id.musicPathTextView, musicPath);
        }
    }

    private void setTextViewFilename(View v, int resourceId, String path) {
        ((TextView) v.findViewById(resourceId)).setText(new File(path).getName());
    }
}
