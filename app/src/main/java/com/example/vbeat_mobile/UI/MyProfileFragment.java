package com.example.vbeat_mobile.UI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.UserManager;
import com.example.vbeat_mobile.backend.user.repository.UserRepository;
import com.example.vbeat_mobile.viewmodel.PostListViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {
    private RecyclerView postsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FeedRecyclerViewAdapter feedAdapter;
    private TextView usernameTextView;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        postsRecyclerView = v.findViewById(R.id.posts_RecyclerView);
        usernameTextView = v.findViewById(R.id.username_textView);
        usernameTextView.setText(FirebaseUserManager.getInstance().getCurrentUser().getDisplayName());
        postsRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        postsRecyclerView.setLayoutManager(layoutManager);


        PostListViewModel postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);

        feedAdapter = new FeedRecyclerViewAdapter(postListViewModel);
        feedAdapter.setActivity(getActivity());

        postsRecyclerView.setAdapter(feedAdapter);

        LiveData<List<PostViewModel>> data;
        data = PostRepository.getInstance().getPostsByUser(FirebaseUserManager.getInstance().getCurrentUser().getUserId());

        data.observeForever(new Observer<List<PostViewModel>>() {
            @Override
            public void onChanged(List<PostViewModel> postViewModels) {
                feedAdapter.addAll(postViewModels);
            }
        });


        return v;
    }

}
