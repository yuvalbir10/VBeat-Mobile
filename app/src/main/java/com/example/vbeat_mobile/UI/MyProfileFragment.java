package com.example.vbeat_mobile.UI;


import android.content.Context;
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
import android.widget.Toast;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.post.repository.PostChangeData;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.UserManager;
import com.example.vbeat_mobile.backend.user.repository.UserRepository;
import com.example.vbeat_mobile.viewmodel.PostListViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;
import com.example.vbeat_mobile.viewmodel.UserViewModel;
import com.google.android.gms.common.UserRecoverableException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Collections;
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
    private ListenerRegistration newPostListenerRegistration = null;

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






        UserRepository.getInstance().getCurrentUser().observeForever(new Observer<UserViewModel>() {
            @Override
            public void onChanged(UserViewModel userViewModel) {
                LiveData<List<PostViewModel>> data;
                data = PostRepository.getInstance().getPostsByUser(userViewModel.getUserId());
                data.observeForever(new Observer<List<PostViewModel>>() {
                    @Override
                    public void onChanged(List<PostViewModel> postViewModels) {
                        feedAdapter.addAll(postViewModels);
                        listenOnPosts(postViewModels);
                        showToastOnNewPost(getFirstPostId(postViewModels));
                    }
                });
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

        feedAdapter.setEditOnClickListener(new FeedRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index, PostViewModel post) {
                View currentView = getView();
                if (currentView == null) {
                    Log.e(TAG, "currentView == null");
                    throw new IllegalStateException(TAG + " currentView == null");
                }

                NavController navController = null;
                navController = Navigation.findNavController(currentView);

                MyProfileFragmentDirections.ActionMyProfileFragmentToEditPostFragment action = MyProfileFragmentDirections.actionMyProfileFragmentToEditPostFragment();
                action.setPostId(post.getPostId());
                navController.navigate(action);
            }
        });


        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(newPostListenerRegistration != null){
            newPostListenerRegistration.remove();
        }
    }

    private void listenOnPosts(List<PostViewModel> postViewModels) {
        for(PostViewModel postViewModel : postViewModels) {
            PostRepository.getInstance().listenToPostChange(postViewModel.getPostId()).observeForever(new Observer<PostChangeData>() {
                @Override
                public void onChanged(PostChangeData postChangeData) {
                    feedAdapter.edit(postChangeData.getPostId(), postChangeData.getNewDescription());
                    if(postChangeData.getIsDeleted()) {
                        feedAdapter.remove(postChangeData.getPostId());
                    }
                }
            });
        }
    }

    private void showToastOnNewPost(String firstPostId){
        if(firstPostId == null){
            return;
        }

        // remove if we're already subscribed
        if(newPostListenerRegistration != null) {
            newPostListenerRegistration.remove();
            // good practice
            newPostListenerRegistration = null;
        }

        final Context c = getContext();

        newPostListenerRegistration = PostRepository.getInstance().listenToNewPost(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "new post was logged");
                if(c != null){
                    Toast.makeText(MyProfileFragment.this.getContext(), getString(R.string.home_button_refresh_new_post) , Toast.LENGTH_SHORT).show();
                }
            }
        }, firstPostId);
    }

    private String getFirstPostId(List<PostViewModel> postViewModels) {
        if(postViewModels.size() == 0){
            return null;
        }

        // same comparator as in the FeedRecyclerViewAdapter
        // should not matter
        Collections.sort(postViewModels, new PostViewModelDateComparator());
        return postViewModels.get(0).getPostId();
    }

}
