package com.example.vbeat_mobile.backend.cache;

public interface Cache<T, K> {
    T get(K key) throws CacheFailException;
}
