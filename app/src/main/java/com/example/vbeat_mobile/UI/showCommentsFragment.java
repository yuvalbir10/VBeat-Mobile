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
public class showCommentsFragment extends Fragment {

    RecyclerView commentsRecyclerView;
    LinearLayoutManager layoutManager;
    public showCommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        showCommentsFragmentArgs args = showCommentsFragmentArgs.fromBundle(getArguments());
        String postID = args.getPostId();

        View view = inflater.inflate(R.layout.fragment_show_comments, container, false);

        commentsRecyclerView = view.findViewById(R.id.comments_RecyclerView);
        commentsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        commentsRecyclerView.setLayoutManager(layoutManager);

        final CommentListRecyclerViewAdapter adapter = new CommentListRecyclerViewAdapter();
        adapter.setActivity(getActivity());

        LiveData<List<CommentViewModel>> mData;
        mData = CommentRepository.getInstance().getComments(postID); //TODO: replace the hardcoded postID with the passed postID from the prev Fragment
        mData.observeForever(new Observer<List<CommentViewModel>>() {
            @Override
            public void onChanged(List<CommentViewModel> commentViewModels) {
                adapter.addAll(commentViewModels); //TODO: change to comments adapter
            }
        });

        commentsRecyclerView.setAdapter(adapter);

        return view;
    }

}
