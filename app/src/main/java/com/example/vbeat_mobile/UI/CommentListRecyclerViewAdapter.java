package com.example.vbeat_mobile.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.viewmodel.CommentViewModel;

import java.util.LinkedList;
import java.util.List;

public class CommentListRecyclerViewAdapter extends RecyclerView.Adapter<CommentListRecyclerViewAdapter.CommentRowViewHolder> {
    List<CommentViewModel> mData;

    public CommentListRecyclerViewAdapter(List<CommentViewModel> data){
        mData = data;
    }

    public CommentListRecyclerViewAdapter(){
        mData = new LinkedList<>();
    }

    @NonNull
    @Override
    public CommentRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent);
        CommentRowViewHolder commentRowViewHolder = new CommentRowViewHolder(view);
        return commentRowViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRowViewHolder holder, int position) {
        CommentViewModel comment = mData.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class CommentRowViewHolder extends RecyclerView.ViewHolder{
        TextView usernameTextView;
        TextView commentTextView;

        public CommentRowViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_textView);
            commentTextView= itemView.findViewById(R.id.comment_textView);
        }

        public void bind(CommentViewModel comment){
            usernameTextView.setText(comment.getUsername());
            commentTextView.setText(comment.getCommentText());
        }

    }

    public void addAll(List<CommentViewModel> moveResults) {
        for (CommentViewModel result : moveResults) {
            add(result);
        }
    }

    public void add(CommentViewModel r) {
        mData.add(r);
        notifyItemInserted(mData.size() - 1);
    }
}
