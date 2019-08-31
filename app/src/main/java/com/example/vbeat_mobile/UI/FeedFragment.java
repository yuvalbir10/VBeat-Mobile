package com.example.vbeat_mobile.UI;


import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.example.vbeat_mobile.backend.user.UserLoginFailedException;
import com.example.vbeat_mobile.viewmodel.PostListViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    private static final String TAG = "FeedFragment";

    private FeedRecyclerViewAdapter feedAdapter;
    private ProgressBar progressBar;

    static MediaPlayer mediaPlayer = new MediaPlayer();

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private static final int TOTAL_PAGES = 100; // TODO: change it according to the DBs total pages
    private int POSTS_PER_PAGE = 5;

    public FeedFragment() {
        // Required empty public constructor
    }

    // temporary for testing purposes only
    // this method blocks so run in a
    // seperate thread
    static byte[] downloadMusic(String musicPath) {
        Task<byte[]> t = FirebaseStorage.getInstance()
                .getReference().child(musicPath).getBytes(10000000);

        try {
            return Tasks.await(t);
        } catch (InterruptedException | ExecutionException e) {
            Log.e("tmp", "download interrupted", e);
            return null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.posts_RecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext()); //TODO : check if i passed the right context
        recyclerView.setLayoutManager(layoutManager);
        progressBar = v.findViewById(R.id.loadmore_progressBar);

        // setting post list view model to hold the data
        // to survive configuration changes
        PostListViewModel postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);

        feedAdapter = new FeedRecyclerViewAdapter(postListViewModel);
        feedAdapter.setActivity(getActivity());

        recyclerView.setAdapter(feedAdapter);

        LiveData<List<PostViewModel>> data;
        data = PostRepository.getInstance().getPosts(null, POSTS_PER_PAGE);
        data.observeForever(new Observer<List<PostViewModel>>() {
            @Override
            public void onChanged(List<PostViewModel> postViewModels) {
                feedAdapter.addAll(postViewModels);
                progressBar.setVisibility(View.INVISIBLE);
                isLoading = false;
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

                FeedFragmentDirections.ActionFeedFragmentToShowCommentsFragment action = FeedFragmentDirections.actionFeedFragmentToShowCommentsFragment();
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

                FeedFragmentDirections.ActionFeedFragmentToEditPostFragment action = FeedFragmentDirections.actionFeedFragmentToEditPostFragment();
                action.setPostId(post.getPostId());
                navController.navigate(action);
            }
        });


        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;

                loadNextPageInBackground();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        return v;
    }

    private void loadNextPageInBackground() {
        //TODO: move to another thread
        Log.d(TAG, "loadNextPageInBackground is called");
        progressBar.setVisibility(View.VISIBLE);

        LiveData<List<PostViewModel>> mData;
        mData = PostRepository.getInstance().getPosts(feedAdapter.getDataList().get(feedAdapter.getDataList().size() - 1).getPostId(), POSTS_PER_PAGE);
        mData.observeForever(new Observer<List<PostViewModel>>() {
            @Override
            public void onChanged(List<PostViewModel> postViewModels) {
                Log.d(TAG, String.format("onChanged called %d", postViewModels.size()));

                feedAdapter.addAll(postViewModels);
                progressBar.setVisibility(View.INVISIBLE);
                isLoading = false;
            }
        });
    }
}
