package com.example.vbeat_mobile.UI;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.vbeat_mobile.R;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadPostFragment extends Fragment {
    ImageView imageView;
    Button chooseImageButton, chooseMusicButton;
    private static final int PICK_IMAGE_REQUEST = 1, PICK_MUSIC_REQUEST = 2;
    Uri imageUri, musicUri;

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

        return v;
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void pickMusicFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_MUSIC_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUri).into(imageView);
        }

        if(requestCode == PICK_MUSIC_REQUEST && resultCode == RESULT_OK){
            musicUri = data.getData();
        }
    }
}
