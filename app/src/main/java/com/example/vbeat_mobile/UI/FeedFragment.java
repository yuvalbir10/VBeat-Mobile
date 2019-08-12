package com.example.vbeat_mobile.UI;


import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.user.UserLoginFailedException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Vector;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    int tempPostNum = 0;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FeedRecyclerViewAdapter feedAdapter;
    ProgressBar progressBar;

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private static final int TOTAL_PAGES = 100; // TODO: change it according to the DBs total pages
    private int currentPage = PAGE_START;

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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = v.findViewById(R.id.posts_RecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext()); //TODO : check if i passed the right context
        recyclerView.setLayoutManager(layoutManager);
        progressBar =  v.findViewById(R.id.loadmore_progressBar);


        //TODO: get the relevant posts list from DB
        Vector<String> mData = new Vector<>();
        for(int i = 0; i < 4; i++){
            tempPostNum++;
            mData.add("st" + tempPostNum);
        }

        feedAdapter = new FeedRecyclerViewAdapter(mData);
        recyclerView.setAdapter(feedAdapter);

        feedAdapter.setOnItemClickListener(new FeedRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index) {
                //TODO: complete what we want to happen when post is clicked (for example go to a page that shows this post only)
                Log.d("TAG", "item click " + index); //TODO: remove this line, this is for checking purposes
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                Activity a = FeedFragment.this.getActivity();
                try {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Vector<String> mData = new Vector<String>();
                    for(int i = 0; i < 4; i++){
                        tempPostNum++;
                        mData.add("st" + tempPostNum);
                    }
                    feedAdapter.addAll(mData);
                    progressBar.setVisibility(View.INVISIBLE);
                    isLoading = false;
                }
                catch(final Exception e) {

                }
                finally {

                    // hide progress bar & show login button
                    safeRunOnUiThread(a, new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }).start();
    }

    private void safeRunOnUiThread(Activity a, Runnable r){
        if(a != null) {
            a.runOnUiThread(r);
        }
    }
}