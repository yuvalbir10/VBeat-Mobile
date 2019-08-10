package com.example.vbeat_mobile.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vbeat_mobile.R;

import java.util.Vector;


public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.PostRowViewHolder> {
    Vector<String> mData; //TODO: change all String objects to Post Objects
    OnItemClickListener clickListener;
    PaginationScrollListener paginationScrollListener;

    public FeedRecyclerViewAdapter(Vector<String> data){
        mData = data;
    }

    interface OnItemClickListener{
        void onClick(int index);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        clickListener = listener;
    }

    public void setPaginationScrollListener(PaginationScrollListener listener){
        paginationScrollListener = listener;
    }

    @NonNull
    @Override
    public PostRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);
        PostRowViewHolder postRowViewHolder = new PostRowViewHolder(view, clickListener);
        return postRowViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostRowViewHolder holder, int position) {
        String str = mData.elementAt(position);
        holder.bind(str);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class PostRowViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePhoto;
        ImageView postImage;
        TextView description;
        TextView username;
        ImageButton musicControlButton;

        public PostRowViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profile_imageView);
            postImage = itemView.findViewById(R.id.post_imageView);
            description = itemView.findViewById(R.id.description_textView);
            username = itemView.findViewById(R.id.username_textView);
            musicControlButton = itemView.findViewById(R.id.musicControl_imageButton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    if(clickListener!= null) {
                        if(index != RecyclerView.NO_POSITION){
                            clickListener.onClick(index);
                        }
                    }
                }
            });
        }

        public void bind(String str){
            username.setText("username: " + str);
        }
    }



    protected class LoadingVH extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;


        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progressBar);


        }

    }




    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(String r) {
        mData.add(r);
        notifyItemInserted(mData.size() - 1);
    }

    public void addAll(Vector<String> moveResults) {
        for (String result : moveResults) {
            add(result);
        }
    }

    public void remove(String r) {
        int position = mData.indexOf(r);
        if (position > -1) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public String getItem(int position) {
        return mData.get(position);
    }

}