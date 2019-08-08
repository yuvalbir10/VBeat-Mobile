package com.example.vbeat_mobile.UI;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vbeat_mobile.R;

public class ViewPostFragment extends Fragment {

    public ViewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        // setting post id to make sure args went in fine
        assert getArguments() != null;
        String postId = ViewPostFragmentArgs.fromBundle(getArguments()).getPostId();
        TextView tv = v.findViewById(R.id.view_post_id_text);
        tv.setText(postId);

        return v;
    }
}
