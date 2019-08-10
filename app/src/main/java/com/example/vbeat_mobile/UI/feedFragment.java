package com.example.vbeat_mobile.UI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.vbeat_mobile.R;

import java.sql.Time;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class feedFragment extends Fragment {
    int tempPostNum = 0;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FeedRecyclerViewAdapter feedAdapter;
    ProgressBar progressBar;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private static final int TOTAL_PAGES = 100;
    private int currentPage = PAGE_START;

    public feedFragment() {
        // Required empty public constructor
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

                loadNextPage();
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

    private void loadNextPage() {
        //TODO: move to another thread
        progressBar.setVisibility(View.VISIBLE);

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
}
