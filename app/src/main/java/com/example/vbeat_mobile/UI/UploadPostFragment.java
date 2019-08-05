package com.example.vbeat_mobile.UI;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.utility.ExifUtil;

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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imageView.setVisibility(View.VISIBLE);

            String path = getRealPathFromURI_API19(getContext(), imageUri);

            // if we're getting a good path
            // load into image view
            if (path != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);

                View v = getView();
                if (v == null) {
                    return;
                }

                // prevent memory issues
                Bitmap scaled = Bitmap.createScaledBitmap(
                        bitmap,
                        512,
                        512,
                        true);

                Bitmap fixed = ExifUtil.rotateBitmap(path, scaled);

                ImageView image = v.findViewById(R.id.imageView);

                image.setImageBitmap(fixed);
                setTextViewFilename(v, R.id.imagePathTextView, path);;
            }
        }

        if (requestCode == PICK_MUSIC_REQUEST && resultCode == RESULT_OK) {
            musicUri = data.getData();
        }
    }

    // min sdk is 19 so we're good
    private static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        if (cursor == null) {
            return null;
        }

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private void setTextViewFilename(View v, int resourceId, String path) {
        ((TextView)v.findViewById(resourceId)).setText(new File(path).getName());
    }
}
