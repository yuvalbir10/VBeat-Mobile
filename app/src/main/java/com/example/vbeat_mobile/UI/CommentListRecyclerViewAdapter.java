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
import com.example.vbeat_mobile.utility.UiUtils;
import com.example.vbeat_mobile.viewmodel.CommentViewModel;

import java.util.LinkedList;
import java.util.List;

public class CommentListRecyclerViewAdapter extends RecyclerView.Adapter<CommentListRecyclerViewAdapter.CommentRowViewHolder> {
    private List<CommentViewModel> mData;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        return new CommentRowViewHolder(view);
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

        CommentRowViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_textView);
            commentTextView= itemView.findViewById(R.id.comment_textView);
            deleteImageButton = itemView.findViewById(R.id.delete_imageButton);
        }

        void bind(final CommentViewModel comment){
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
                                    remove(comment.getCommentId());
                                    UiUtils.showMessage(fromActivity, "Deleted comment successfully!");
                                } catch (final CommentException e) {
                                    UiUtils.showMessage(fromActivity, "Error on deleting comment..." + e.getMessage());
                                }
                            }
                        }).start();
                    }
                });
            }
        }

    }

    void addAll(List<CommentViewModel> moveResults) {
        for (CommentViewModel result : moveResults) {
            add(result);
        }
    }

    public void add(CommentViewModel r) {
        mData.add(r);
        notifyItemInserted(mData.size() - 1);
    }

    public void remove(String commentId) {
        int position = findPositionById(commentId);
        if (position > -1) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setList(List<CommentViewModel> commentList) {
        mData = commentList;
        notifyDataSetChanged();
    }

    private int findPositionById(String commentId){
        for (int i = 0; i < mData.size(); i++){
            if(mData.get(i).getCommentId().contentEquals(commentId))
                return i;
        }
        return -1;
    }
}
