package com.example.vbeat_mobile.UI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.example.vbeat_mobile.viewmodel.UserViewModel;
import com.google.android.gms.common.UserRecoverableException;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {
    private static final String TAG = "MyProfileFragment";
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


        UserRepository.getInstance().getCurrentUser().observeForever(new Observer<UserViewModel>() {
            @Override
            public void onChanged(UserViewModel userViewModel) {
                usernameTextView.setText(
                    userViewModel.getDisplayName()
                );
            }
        });

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

        feedAdapter.setOnItemClickListener(new FeedRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index, PostViewModel post) {
                Log.d("TAG", "item click " + index);
                View currentView = getView();
                if (currentView == null) {
                    Log.e(TAG, "currentView == null");
                    throw new IllegalStateException(TAG + " currentView == null");
                }

                NavController navController = null;
                navController = Navigation.findNavController(currentView);

                MyProfileFragmentDirections.ActionMyProfileFragmentToShowCommentsFragment action = MyProfileFragmentDirections.actionMyProfileFragmentToShowCommentsFragment();
                action.setPostId(post.getPostId());
                navController.navigate(action);
            }
        });


        return v;
    }

}
