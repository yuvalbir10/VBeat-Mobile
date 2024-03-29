package com.example.vbeat_mobile.UI;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.example.vbeat_mobile.utility.ImageViewUtil;
import com.example.vbeat_mobile.utility.URIUtils;
import com.example.vbeat_mobile.utility.UiUtils;
import com.example.vbeat_mobile.viewmodel.PostViewModel;

import java.io.File;
import java.io.IOException;

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
    private Button postButton;
    private EditText descriptionEditText;
    private ProgressBar prBar;

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
        postButton = v.findViewById(R.id.post_button);
        descriptionEditText = v.findViewById(R.id.description_editText);
        prBar = v.findViewById(R.id.progressBar);


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


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPostInBackground();
            }
        });

        return v;
    }

    private void uploadPostInBackground() {
        final View v = getView();

        if (v == null) {
            throw new IllegalStateException("no view available can't upload post in background");
        }

        //get descriptionTextView String from UI
        final String description = descriptionEditText.getText().toString();

        if (musicUri == null || imageUri == null) {
            Toast.makeText(getContext(), "Please choose image & music!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // show progress bar & disable post button
        postButton.setEnabled(false);
        prBar.setVisibility(View.VISIBLE);

        // create post in background so
        // ui won't be stuck!
        new Thread(new Runnable() {
            @Override
            public void run() {
                Activity a = UploadPostFragment.this.getActivity();
                final VBeatPostModel uploadedPost = PostViewModel.uploadPost(description, imageUri, musicUri);
                if(uploadedPost != null){
                    UiUtils.safeRunOnUiThread(a, new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadPostFragment.this.getContext(),
                                    "Uploaded Post successfully!",
                                    Toast.LENGTH_SHORT).show();

                            handleUploadPostFinish(uploadedPost);
                        }
                    });
                }
                else{
                    //error if upload post failed
                    final TextView errorTextView = v.findViewById(R.id.error_textView);
                    UiUtils.safeRunOnUiThread(a, new Runnable() {
                        @Override
                        public void run() {
                            errorTextView.setText("Upload post failed");
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                    });
                }
                // hide progress bar & show login button
                UiUtils.safeRunOnUiThread(a, new Runnable() {
                    @Override
                    public void run() {
                        postButton.setEnabled(true);
                        prBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    private void handleUploadPostFinish(VBeatPostModel uploadedPost) {
        String postId = uploadedPost.getPostId();
        UploadPostFragmentDirections.ActionUploadPostFragmentToViewPostFragment action =
                UploadPostFragmentDirections.actionUploadPostFragmentToViewPostFragment()
                .setPostId(postId);

        View curView = getView();
        if(curView == null){
            throw new IllegalStateException("view == null while handleUploadPostFinish");
        }

        Navigation.findNavController(curView).navigate(action);
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

            String path = null;
            try {
                path = URIUtils.copyAndGetPath(imageUri, getContext());
            } catch (IOException e) {
                Log.e(TAG, "copyAndGetPath failed", e);
            }

            // if we're getting a good path
            // load into image view
            if (path != null) {
                imageUri = Uri.fromFile(new File(path));
                ImageView imageView = v.findViewById(R.id.imageView);

//                // prevent memory issues
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                Bitmap scaled = Bitmap.createScaledBitmap(
//                        bitmap,
//                        512,
//                        512,
//                        true);
//
//                Bitmap fixed = ExifUtil.rotateBitmap(path, scaled);
//
//
//                imageView.setImageBitmap(fixed);
                ImageViewUtil.getInstance().displayAndCache(
                        getActivity(),
                        imageView,
                        imageUri
                );
                setTextViewFilename(v, R.id.imagePathTextView, path);
            }
        }

        if (requestCode == PICK_MUSIC_REQUEST && resultCode == RESULT_OK) {
            // content uri
            musicUri = data.getData();

            String musicPath = null;
            try {
                musicPath = URIUtils.copyAndGetPath(musicUri, getContext());
            } catch (IOException e) {
                Log.e(TAG, "copyAndGetPath failed on music copy", e);
            }

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
