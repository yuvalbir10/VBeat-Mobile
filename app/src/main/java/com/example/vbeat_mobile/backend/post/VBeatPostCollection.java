package com.example.vbeat_mobile.backend.post;

import java.util.List;

public class VBeatPostCollection<T> {
    private List<VBeatPostModel> posts;

    private T cursor;

    public VBeatPostCollection(List<VBeatPostModel> posts, T cursor){
        this.posts = posts;
        this.cursor = cursor;
    }

    public T getCursor() {
        return cursor;
    }

    public List<VBeatPostModel> getPosts() {
        return posts;
    }
}
