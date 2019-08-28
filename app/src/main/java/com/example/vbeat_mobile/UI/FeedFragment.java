package com.example.vbeat_mobile.UI;


import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import com.example.vbeat_mobile.backend.post.FirebasePostManager;
import com.example.vbeat_mobile.backend.post.VBeatPostModel;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.example.vbeat_mobile.backend.user.UserLoginFailedException;
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
    private static final String TAG  = "FeedFragment";

    int tempPostNum = 0;
    FirebasePostManager firebasePostManager;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FeedRecyclerViewAdapter feedAdapter;
    private ProgressBar progressBar;

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private static final int TOTAL_PAGES = 100; // TODO: change it according to the DBs total pages
    private int currentPage = PAGE_START;
    private int POSTS_PER_PAGE = 2;

    public FeedFragment() {
        // Required empty public constructor
    }

    // temporary for testing purposes only
    // this method blocks so run in a
    // seperate thread
    public static byte[] downloadMusic(String musicPath) {
        Task<byte[]> t = FirebaseStorage.getInstance()
                .getReference().child(musicPath).getBytes(10000000);

        try {
            return Tasks.await(t);
        } catch(InterruptedException | ExecutionException e ){
            Log.e("tmp", "download interrupted", e);
            return null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         firebasePostManager = FirebasePostManager.getInstance();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = v.findViewById(R.id.posts_RecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext()); //TODO : check if i passed the right context
        recyclerView.setLayoutManager(layoutManager);
        progressBar =  v.findViewById(R.id.loadmore_progressBar);

        feedAdapter = new FeedRecyclerViewAdapter();
        feedAdapter.setActivity(getActivity());
        recyclerView.setAdapter(feedAdapter);

        //TODO: get the relevant posts list from DB
        LiveData<List<PostViewModel>> mData;
        mData = PostRepository.getInstance().getPosts(null, POSTS_PER_PAGE);
        mData.observeForever(new Observer<List<PostViewModel>>() {
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
                //TODO: complete what we want to happen when post is clicked (for example go to a page that shows this post only)
                Log.d("TAG", "item click " + index); //TODO: remove this line, this is for checking purposes
                View currentView = getView();
                if(currentView == null) {
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


        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

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
        progressBar.setVisibility(View.VISIBLE);

            LiveData<List<PostViewModel>> mData;
            mData = PostRepository.getInstance().getPosts(feedAdapter.mData.get(feedAdapter.mData.size()-1).getPostId(), POSTS_PER_PAGE);
            mData.observeForever(new Observer<List<PostViewModel>>() {
                @Override
                public void onChanged(List<PostViewModel> postViewModels) {
                    feedAdapter.addAll(postViewModels);
                    progressBar.setVisibility(View.INVISIBLE);
                    isLoading = false;
                }
            });
    }

    private void safeRunOnUiThread(Activity a, Runnable r){
        if(a != null) {
            a.runOnUiThread(r);
        }
    }

    private PostViewModel[] PostBackendToFront(VBeatPostModel[] posts){
        PostViewModel [] ret = new PostViewModel[posts.length];
        for(int i =0; i<posts.length;i++){
            ret[i] = new PostViewModel(posts[i].getPostId(),posts[i].getDescription(),posts[i].getRemoteImagePath(),posts[i].getRemoteMusicPath(),posts[i].getUploaderId());
        }
        return ret;
    }
}
