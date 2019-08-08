package com.example.vbeat_mobile.backend.cache;

public interface Cache<T, K> {
    public T get(K key);
}
