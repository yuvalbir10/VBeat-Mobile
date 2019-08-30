package com.example.vbeat_mobile.UI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.comment.repository.CommentRepository;
import com.example.vbeat_mobile.viewmodel.CommentViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowCommentsFragment extends Fragment {

    public ShowCommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ShowCommentsFragmentArgs args = ShowCommentsFragmentArgs.fromBundle(getArguments());
        String postID = args.getPostId();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_comments, container, false);

        RecyclerView commentsRecyclerView = view.findViewById(R.id.comments_RecyclerView);
        commentsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        commentsRecyclerView.setLayoutManager(layoutManager);

        final CommentListRecyclerViewAdapter adapter = new CommentListRecyclerViewAdapter();
        adapter.setActivity(getActivity());

        LiveData<List<CommentViewModel>> mData;
        mData = CommentRepository.getInstance().getComments(postID);
        mData.observeForever(new Observer<List<CommentViewModel>>() {
            @Override
            public void onChanged(List<CommentViewModel> commentViewModels) {
                adapter.addAll(commentViewModels);
            }
        });

        commentsRecyclerView.setAdapter(adapter);

        return view;
    }

}
