package com.example.vbeat_mobile.backend.post;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.vbeat_mobile.backend.user.FirebaseUserManager;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FirebasePostManager implements PostManager<String> {
    private static final String TAG = "FirebasePostManager";
    private static final String postCollectionName = "posts";
    private FirebaseFirestore db;

    // if we're using the firebase post manager
    // it's safe to say that we're using the
    // firebase user manager
    private FirebaseUserManager userManager;

    private static class FirebasePostManagerInstanceHolder {
        private static FirebasePostManager instance = new FirebasePostManager();
    }

    public static FirebasePostManager getInstance(){
        return FirebasePostManagerInstanceHolder.instance;
    }

    private FirebasePostManager(){
        userManager = FirebaseUserManager.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public VBeatPostModel uploadPost(String description, Uri imageUri, Uri musicUri) throws UploadPostFailedException {
        if(!userManager.isUserLoggedIn()) {
            throw new UploadPostFailedException("user not logged in");
        }

        if(description == null || description.length() == 0) {
            throw new UploadPostFailedException("no description to post");
        }

        if(!sanityCheckFile(imageUri) || !sanityCheckFile(musicUri)) {
            throw new UploadPostFailedException("unable to access image or music file");
        }


        String remoteImagePath = null;
        try {
            remoteImagePath = uploadImage(imageUri);
        } catch(IOException e) {
            throw new UploadPostFailedException(e.getMessage());
        }

        String remoteMusicPath = null;
        try {
            remoteMusicPath = uploadMusic(musicUri);
        } catch(IOException e){
            throw new UploadPostFailedException(e.getMessage());
        }

        Map<String, Object> firebaseMap = FirebasePostAdapter.toFirebaseMap(
                    description,
                    remoteImagePath,
                    remoteMusicPath,
                    userManager.getCurrentUser()
                );

        DocumentReference docRef = null;
        try {
             docRef = Tasks.await(db.collection(postCollectionName).add(firebaseMap));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "upload post interrupted", e);
            throw new UploadPostFailedException(e.getMessage());
        }

        return new FirebasePostAdapter(
                docRef.getId(),
                description,
                remoteImagePath,
                remoteMusicPath,
                userManager.getCurrentUser().getUserId()
        );
    }

    @Override
    public VBeatPostModel getPost(String postId) {
        try {
            DocumentSnapshot documentSnapshot = Tasks.await(
                    db.collection(postCollectionName).document(postId).get()
            );
            return new FirebasePostAdapter(documentSnapshot);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "getPost interrupted", e);
            return null;
        }
    }

    // id of the last post
    @Override
    public VBeatPostCollection<String> getPosts(String cursor, int limit) {
        // might not work we'll have to test
        DocumentSnapshot lastPostRendered;
        QuerySnapshot nextPostsQuery;
        try {
            if(cursor==null){
                nextPostsQuery = Tasks.await(
                        db.collection(postCollectionName).limit(limit).get());
            }
            else{
                lastPostRendered = Tasks.await(db.collection(postCollectionName).document(cursor).get());
                nextPostsQuery = Tasks.await(
                        db.collection(postCollectionName).startAfter(lastPostRendered).limit(limit).get());
            }

            // get n (limit) posts after the current post mentioned in cursor
            LinkedList<VBeatPostModel> vbeatPostList =  new LinkedList<>();

            for (DocumentSnapshot snapshot : nextPostsQuery.getDocuments()) {
                vbeatPostList.add(new FirebasePostAdapter(snapshot));
            }

            String lastPostId = null;
            if(vbeatPostList.size() != 0){
                lastPostId = vbeatPostList.getLast().getPostId();
            }

            return new VBeatPostCollection<>(vbeatPostList, lastPostId);
        } catch (ExecutionException | InterruptedException  e) {
            e.printStackTrace();
            Log.d(TAG, "getPosts interrupted", e);
            return null;
        }
    }


    //ishay please implement this method
    @Override
    public void deletePost(String postId) throws DeletePostException {

    }

    // time to check
    // time to use
    // still a useful sanity check
    private boolean sanityCheckFile(Uri filePath){
        return filePath != null && filePath.getPath() != null && new File(filePath.getPath()).exists();
    }

    // uploads image and returns path
    private String uploadImage(Uri localImagePath) throws IOException {
        Bitmap imageBitmap = BitmapFactory.decodeFile(localImagePath.getPath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // save storage space
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

        byte[] imageByteArray = bos.toByteArray();
        return uploadBytes(imageByteArray, createImageFilename(localImagePath));
    }

    // uploads music and returns path
    private String uploadMusic(Uri localMusicPath) throws IOException {
        if(localMusicPath.getPath() == null){
            throw new IOException("null music path");
        }

        byte[] musicFileBArr = readFile(new File(localMusicPath.getPath()));
        return uploadBytes(musicFileBArr, createMusicFilename(localMusicPath));
    }

    // uploads byte array and returns path
    private String uploadBytes(byte[] bArr, String filename) throws IOException {
        FirebaseStorage instance = FirebaseStorage.getInstance();

        StorageReference rootRef = instance.getReference();
        StorageReference fileRef = rootRef.child(filename);

        UploadTask.TaskSnapshot t;
        try {
             t = Tasks.await(fileRef.putBytes(bArr));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Tasks.await was interrupted during file upload", e);

            throw new IOException("interrupted file upload", e);
        }

        if(t.getMetadata() != null) {
            return t.getMetadata().getPath();
        } else {
            throw new IOException("file upload failed due to unknown error");
        }
    }

    private String createImageFilename(Uri localImagePath){
        return createFolderFilename("images", localImagePath);
    }

    private String createMusicFilename(Uri localMusicPath) {
        return createFolderFilename("music", localMusicPath);
    }

    private String createFolderFilename(String folderName, Uri localFile){
        String filename = getRandomUUID();

        if(localFile.getPath() != null ){
            filename = new File(localFile.getPath()).getName();
        }

        return String.format(
                "%s/%s/%s",
                folderName,
                getRandomUUID(),
                filename
        );
    }

    private String getRandomUUID(){
        return UUID.randomUUID().toString();
    }

    private static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
