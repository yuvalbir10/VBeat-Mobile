package com.example.vbeat_mobile.UI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vbeat_mobile.R;

import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class feedFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FeedRecyclerViewAdapter feedAdapter;
    Vector<String> mData = new Vector<String>();

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


        //TODO: get the relevant posts list from DB

        for(int i = 0; i < 100; i++){
            mData.add("st" + i);
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

        return v;
    }

}
