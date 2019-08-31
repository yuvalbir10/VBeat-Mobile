package com.example.vbeat_mobile.UI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vbeat_mobile.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {
    RecyclerView postsRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        postsRecyclerView = v.findViewById(R.id.posts_RecyclerView);
        postsRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        postsRecyclerView.setLayoutManager(layoutManager);

        return v;
    }

}
