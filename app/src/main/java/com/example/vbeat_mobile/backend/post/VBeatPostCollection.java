package com.example.vbeat_mobile.backend.post;

import java.util.List;

public class VBeatPostCollection<T> {
    private List<VBeatPost> posts;

    private T cursor;

    public VBeatPostCollection(List<VBeatPost> posts, T cursor){
        this.posts = posts;
        this.cursor = cursor;
    }

    public T getCursor() {
        return cursor;
    }

    public List<VBeatPost> getPosts() {
        return posts;
    }
}
