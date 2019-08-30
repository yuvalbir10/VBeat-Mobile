package com.example.vbeat_mobile.UI;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.comment.CommentException;
import com.example.vbeat_mobile.backend.comment.FirebaseCommentManager;
import com.example.vbeat_mobile.backend.post.DeletePostException;
import com.example.vbeat_mobile.backend.post.FirebasePostManager;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.utility.ImageViewUtil;
import com.example.vbeat_mobile.utility.UiUtils;
import com.example.vbeat_mobile.viewmodel.PostListViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.PostRowViewHolder> {
    private static final String TAG = "FeedRecyclerVA";

    private PostListViewModel mData;
    private OnItemClickListener clickListener;
    private Activity fromActivity;

    public FeedRecyclerViewAdapter(PostListViewModel data) {
        mData = data;
        mData.setPostList(new ArrayList<PostViewModel>());
    }

    public void setActivity(Activity a) {
        fromActivity = a;
    }

    interface OnItemClickListener {
        void onClick(int index, PostViewModel post);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public PostRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);
        return new PostRowViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRowViewHolder holder, int position) {
        PostViewModel post = getDataList().get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    class PostRowViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView description;
        TextView username;
        final ImageButton musicControlButton;
        Button commentButton;
        EditText commentEditText;
        String postId;
        ImageButton deleteButton;

        PostRowViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_imageView);
            description = itemView.findViewById(R.id.description_textView);
            username = itemView.findViewById(R.id.username_textView);
            musicControlButton = itemView.findViewById(R.id.musicControl_imageButton);
            commentButton = itemView.findViewById(R.id.post_comment_button);
            commentEditText = itemView.findViewById(R.id.comment_editText);
            deleteButton = itemView.findViewById(R.id.delete_imageButton);
        }

        void bind(final PostViewModel post) {
            username.setText(post.getUploader());
            description.setText(post.getDescription());
            postId = post.getPostId();

            if(post.getUploader().contentEquals(FirebaseUserManager.getInstance().getCurrentUser().getUserId())){
                deleteButton.setVisibility(View.VISIBLE);
            }

            downloadAndDisplayImageInBackground(post);

            setupMusicButton(post);
            setupDeleteButton();
            setupCommentButton();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    if (clickListener != null) {
                        if (index != RecyclerView.NO_POSITION) {
                            clickListener.onClick(index, getItem(index));
                        }
                    }
                }
            });


        }

        private void downloadAndDisplayImageInBackground(final PostViewModel post) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getAndDownloadImage(post);
                            } catch (ExecutionException | InterruptedException e) {
                                Log.e(TAG, "can't display image", e);
                            }
                        }
                    }
            ).start();
        }

        private void setupCommentButton() {
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final FirebaseCommentManager commentManager = FirebaseCommentManager.getInstance();
                    final String commentStr = commentEditText.getText().toString();

                    if(commentStr.contentEquals("")){
                        UiUtils.showMessage(fromActivity, "Can't post empty comment...");
                        return;
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                commentManager.comment(commentStr, postId);
                                UiUtils.showMessage(fromActivity, "Commented Successfully!");

                                UiUtils.safeRunOnUiThread(fromActivity, new Runnable() {
                                    @Override
                                    public void run() {
                                        commentEditText.setText("");
                                    }
                                });

                            } catch (final CommentException e) {
                                UiUtils.showMessage(fromActivity, "Error Commenting : " + e.getMessage());
                            }
                        }
                    }).start();

                }
            });
        }

        private void setupDeleteButton() {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FirebasePostManager.getInstance().deletePost(postId);
                                UiUtils.showMessage(fromActivity, "Post deleted successfully!");
                            } catch (DeletePostException | CommentException e) {
                                UiUtils.showMessage(fromActivity, "Error on deleting post...");
                            }
                        }
                    }).start();
                }
            });
        }

        private void setupMusicButton(final PostViewModel post) {
            musicControlButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (FeedFragment.mediaPlayer.isPlaying()) {
                                FeedFragment.mediaPlayer.stop();
                            } else {
                                try {
                                    byte[] musicBytes = FeedFragment.downloadMusic(post.getRemoteMusicPath());
                                    playMp3(musicBytes);
                                } catch (Exception e) {
                                    Log.e("FeedFragment", "cant find music path");
                                }
                            }
                        }

                    });
                    t.start();
                }
            });
        }

        private void getAndDownloadImage(PostViewModel post) throws ExecutionException, InterruptedException {
            Uri remoteImageDownloadUri;
            remoteImageDownloadUri = Tasks.await(
                    FirebaseStorage.getInstance()
                            .getReference().child(post.getRemoteImagePath()).getDownloadUrl()
            );

            ImageViewUtil.getInstance().displayAndCache(
                    FeedRecyclerViewAdapter.this.fromActivity,
                    postImage,
                    remoteImageDownloadUri
            );
        }
    }

    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    private boolean isPostInList(String postId) {
        for(PostViewModel postViewModel : getDataList() ){
            if (postId.equals(postViewModel.getPostId())){
                return true;
            }
        }
        return false;
    }

    public void add(PostViewModel r) {
        // this is a sanity check
        // the real check should happen when
        // you ask the PostRepository for more posts
        // and get less than you expected
        // you should stop asking for more posts
        if(!isPostInList(r.getPostId())) {
            getDataList().add(r);
        }

        notifyItemInserted(getDataList().size() - 1);
    }

    public void addAll(List<PostViewModel> moveResults) {
        for (PostViewModel result : moveResults) {
            add(result);
        }
    }

    public void remove(PostViewModel r) {
        int position = getDataList().indexOf(r);
        if (position > -1) {
            getDataList().remove(position);
            notifyItemRemoved(position);
        }
    }

    private PostViewModel getItem(int position) {
        return getDataList().get(position);
    }

    public List<PostViewModel> getDataList(){
        return Objects.requireNonNull(mData.getPostList());
    }

    private static void playMp3(byte[] mp3SoundByteArray) {
        MediaPlayer mediaPlayer = FeedFragment.mediaPlayer;
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("temp_music_file", "mp3");
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            Log.e(TAG, "exception while trying to play music", ex);
        }
    }

}