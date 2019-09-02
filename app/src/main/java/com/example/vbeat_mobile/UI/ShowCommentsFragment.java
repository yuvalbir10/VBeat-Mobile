package com.example.vbeat_mobile.UI;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.comment.repository.CommentRepository;
import com.example.vbeat_mobile.viewmodel.CommentListViewModel;
import com.example.vbeat_mobile.viewmodel.CommentViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;

import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowCommentsFragment extends Fragment {
    private CommentListViewModel commentListViewModel;

    public ShowCommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // parse arguments for this fragment
        // arguments cannot be null because there are no comments
        // to show if arguments are null
        ShowCommentsFragmentArgs args = ShowCommentsFragmentArgs
                .fromBundle(Objects.requireNonNull(getArguments()));
        String postID = args.getPostId();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_comments, container, false);

        // Get view model for this fragment
        commentListViewModel = ViewModelProviders.of(this)
                .get(CommentListViewModel.class);

        RecyclerView commentsRecyclerView = view.findViewById(R.id.comments_RecyclerView);
        // size of recycler view does not change because
        // of each comment
        commentsRecyclerView.setHasFixedSize(true);

        // set layout manager for comment list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        commentsRecyclerView.setLayoutManager(layoutManager);

        final CommentListRecyclerViewAdapter adapter = new CommentListRecyclerViewAdapter();
        adapter.setActivity(getActivity());

        // listen to live changes in comments
        CommentRepository.getInstance().listenOnLiveCommentChanges(postID).observe(this, new Observer<List<CommentViewModel>>() {
            @Override
            public void onChanged(List<CommentViewModel> commentViewModels) {
                adapter.setList(commentViewModels);
            }
        });

        commentsRecyclerView.setAdapter(adapter);

        return view;
    }
}
