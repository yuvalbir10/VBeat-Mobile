package com.example.vbeat_mobile.UI;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.sip.SipSession;
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
import android.widget.Toast;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.backend.post.repository.PostCache;
import com.example.vbeat_mobile.backend.post.repository.PostChangeData;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.example.vbeat_mobile.backend.user.UserLoginFailedException;
import com.example.vbeat_mobile.viewmodel.PostListViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Collections;
import java.util.LinkedList;
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
    private List<Observer> observerList;

    static MediaPlayer mediaPlayer = new MediaPlayer();

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private static final int TOTAL_PAGES = 100; // TODO: change it according to the DBs total pages
    private int POSTS_PER_PAGE = 5;

    // in order to signal firebase
    // when to unsubscribe from a certain
    // new post listener
    private ListenerRegistration newPostListenerRegistration = null;

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

        // set up recycler view
        RecyclerView recyclerView = v.findViewById(R.id.posts_RecyclerView);
        recyclerView.setHasFixedSize(true);

        // set up layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        progressBar = v.findViewById(R.id.loadmore_progressBar);

        // setting post list view model to hold the data
        // to survive configuration changes
        PostListViewModel postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);

        observerList = new LinkedList<>();

        feedAdapter = new FeedRecyclerViewAdapter(postListViewModel);
        feedAdapter.setActivity(getActivity());

        recyclerView.setAdapter(feedAdapter);

        LiveData<List<PostViewModel>> data;
        // get first posts on feed
        data = PostListViewModel.getPosts(null, POSTS_PER_PAGE);

        // wait for posts to load
        data.observeForever(new Observer<List<PostViewModel>>() {
            @Override
            public void onChanged(List<PostViewModel> postViewModels) {
                feedAdapter.addAll(postViewModels);

                // show toast whenever new post is made
                showToastOnNewPost(getFirstPostId(postViewModels));

                // set listener to update posts in live mode
                listenOnPosts(postViewModels);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(newPostListenerRegistration != null) {
            newPostListenerRegistration.remove();
        }
    }

    private void listenOnPosts(List<PostViewModel> postViewModels) {
        for(PostViewModel postViewModel : postViewModels) {
            PostRepository.getInstance().listenToPostChange(postViewModel.getPostId()).observe(this, new Observer<PostChangeData>() {
                @Override
                public void onChanged(final PostChangeData postChangeData) {
                    feedAdapter.edit(postChangeData.getPostId(), postChangeData.getNewDescription());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PostRepository.getInstance().getPostCache()
                                    .updatePost(postChangeData.getPostId(), postChangeData.getNewDescription());
                        }
                    }).start();

                    if(postChangeData.getIsDeleted()) {
                        feedAdapter.remove(postChangeData.getPostId());
                    }
                }
            });
        }
    }

    // side effect is sorting the list
    private String getFirstPostId(List<PostViewModel> postViewModels) {
        if(postViewModels.size() == 0){
            return null;
        }

        // same comparator as in the FeedRecyclerViewAdapter
        // should not matter
        Collections.sort(postViewModels, new PostViewModelDateComparator());
        return postViewModels.get(0).getPostId();
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

        final Context feedContext = FeedFragment.this.getContext();

        newPostListenerRegistration = PostRepository.getInstance().listenToNewPost(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "new post was logged by PostRepository");
                if(feedContext != null) {
                    Toast.makeText(feedContext, feedContext.getString(R.string.home_button_refresh_new_post) , Toast.LENGTH_SHORT).show();
                }
            }
        }, firstPostId);
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


                // set listener to update posts in live mode
                listenOnPosts(postViewModels);

                progressBar.setVisibility(View.INVISIBLE);
                isLoading = false;
            }
        });
    }
}
