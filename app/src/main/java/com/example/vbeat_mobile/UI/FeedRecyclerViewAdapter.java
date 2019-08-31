package com.example.vbeat_mobile.UI;

import android.app.Activity;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vbeat_mobile.R;
import com.example.vbeat_mobile.backend.comment.CommentException;
import com.example.vbeat_mobile.backend.comment.FirebaseCommentManager;
import com.example.vbeat_mobile.backend.post.DeletePostException;
import com.example.vbeat_mobile.backend.post.FirebasePostManager;
import com.example.vbeat_mobile.backend.post.repository.PostChangeData;
import com.example.vbeat_mobile.backend.post.repository.PostRepository;
import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.example.vbeat_mobile.backend.user.repository.UserRepository;
import com.example.vbeat_mobile.utility.ImageViewUtil;
import com.example.vbeat_mobile.utility.UiUtils;
import com.example.vbeat_mobile.viewmodel.PostListViewModel;
import com.example.vbeat_mobile.viewmodel.PostViewModel;
import com.example.vbeat_mobile.viewmodel.UserViewModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        TextView descriptionTextView;
        TextView usernameTextView;
        final ImageButton musicControlButton;
        Button commentButton;
        EditText commentEditText;
        String postId;
        ImageButton deleteButton;

        PostRowViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_imageView);
            descriptionTextView = itemView.findViewById(R.id.description_textView);
            usernameTextView = itemView.findViewById(R.id.username_textView);
            musicControlButton = itemView.findViewById(R.id.musicControl_imageButton);
            commentButton = itemView.findViewById(R.id.post_comment_button);
            commentEditText = itemView.findViewById(R.id.comment_editText);
            deleteButton = itemView.findViewById(R.id.delete_imageButton);
        }

        void bind(final PostViewModel post) {

            final LiveData<UserViewModel> liveUser = UserRepository.getInstance()
                    .getUser(post.getUploader());
            liveUser.observeForever(new Observer<UserViewModel>() {
                        @Override
                        public void onChanged(UserViewModel userViewModel) {
                            usernameTextView.setText(userViewModel.getDisplayName());
                            liveUser.removeObserver(this);
                        }
                    });

            descriptionTextView.setText(post.getDescription());
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


            PostRepository.getInstance().listenToPostChange(postId).observeForever(new Observer<PostChangeData>() {
                @Override
                public void onChanged(PostChangeData postChangeData) {
                    edit(postId,postChangeData.getNewDescription());
                    if(postChangeData.getIsDeleted()){
                        remove(postId);
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
                                
                                remove(postId);
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
            notifyItemInserted(getDataList().size() - 1);
        }
    }

    private List<PostViewModel> sortByDate(List<PostViewModel> postViewModels){
        Collections.sort(postViewModels, new Comparator<PostViewModel>() {
            @Override
            public int compare(PostViewModel postViewModel, PostViewModel t1) {
                if(postViewModel.getUploadDate().before(t1.getUploadDate())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return postViewModels;
    }

    public void addAll(List<PostViewModel> moveResults) {
        moveResults = sortByDate(moveResults);

        for (PostViewModel result : moveResults) {
            add(result);
        }
    }

    public void remove(String postId) {
        int position = findPositionById(postId);
        if (position > -1) {
            getDataList().remove(position);
            notifyItemRemoved(position);
        }
    }

    public void edit(String postId, String newDesc){
        int position = findPositionById(postId);
        if (position > -1) {
            getDataList().get(position).setDescription(newDesc);
            notifyItemChanged(position);
        }
    }


    private int findPositionById(String postId){
        for (int i = 0; i < getDataList().size(); i++){
            if(getDataList().get(i).getPostId().contentEquals(postId))
                return i;
        }
        return -1;
    }

    public void clear() {
            while (getItemCount() > 0) {
                remove(getItem(0).getPostId());
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