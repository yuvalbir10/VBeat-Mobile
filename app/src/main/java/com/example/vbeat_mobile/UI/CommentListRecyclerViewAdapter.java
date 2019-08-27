package com.example.vbeat_mobile.UI;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.comment.CommentException;
import com.example.vbeat_mobile.backend.comment.FirebaseCommentManager;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.viewmodel.CommentViewModel;

import java.util.LinkedList;
import java.util.List;

public class CommentListRecyclerViewAdapter extends RecyclerView.Adapter<CommentListRecyclerViewAdapter.CommentRowViewHolder> {
    List<CommentViewModel> mData;
    private Activity fromActivity;

    public void setActivity(Activity a) {
        fromActivity = a;
    }

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

    class CommentRowViewHolder extends RecyclerView.ViewHolder{
        TextView usernameTextView;
        TextView commentTextView;
        ImageButton deleteImageButton;

        public CommentRowViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_textView);
            commentTextView= itemView.findViewById(R.id.comment_textView);
            deleteImageButton = itemView.findViewById(R.id.delete_imageButton);
        }

        public void bind(final CommentViewModel comment){
            usernameTextView.setText(comment.getUsername());
            commentTextView.setText(comment.getCommentText());
            if(comment.getUserId().contentEquals(FirebaseUserManager.getInstance().getCurrentUser().getUserId())){
                deleteImageButton.setVisibility(View.VISIBLE);
                deleteImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FirebaseCommentManager.getInstance().deleteComment(comment.getCommentId());
                                    safeRunOnUiThread(fromActivity, new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(fromActivity.getBaseContext(),
                                                    "Deleted comment successfully!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (final CommentException e) {
                                    safeRunOnUiThread(fromActivity, new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(fromActivity.getBaseContext(),
                                                    "Error on deleting comment..." + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                });
            }
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

    private void safeRunOnUiThread(Activity a, Runnable r){
        if(a != null) {
            a.runOnUiThread(r);
        }
    }
}
