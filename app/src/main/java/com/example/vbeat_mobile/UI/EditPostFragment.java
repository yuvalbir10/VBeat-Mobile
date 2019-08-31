package com.example.vbeat_mobile.UI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.comment.CommentException;
import com.example.vbeat_mobile.backend.post.DeletePostException;
import com.example.vbeat_mobile.backend.post.FirebasePostManager;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.example.vbeat_mobile.utility.UiUtils;
import com.example.vbeat_mobile.viewmodel.PostViewModel;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditPostFragment extends Fragment {
    EditText descriptionEditText;
    Button saveButton;

    public EditPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        descriptionEditText = view.findViewById(R.id.description_editText);
        saveButton = view.findViewById(R.id.save_button);

        ShowCommentsFragmentArgs args = ShowCommentsFragmentArgs
                .fromBundle(Objects.requireNonNull(getArguments()));
        final String postID = args.getPostId();

        LiveData<PostViewModel> post = PostRepository.getInstance().getPost(postID);
        post.observeForever(new Observer<PostViewModel>() {
            @Override
            public void onChanged(PostViewModel postViewModel) {
                descriptionEditText.setText(postViewModel.getDescription());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PostRepository.getInstance().editPost(postID, descriptionEditText.getText().toString());
                    }
                }).start();
            }
        });



        return view;
    }

}
