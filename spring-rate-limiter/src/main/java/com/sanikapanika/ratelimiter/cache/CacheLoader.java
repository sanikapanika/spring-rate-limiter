package com.sanikapanika.ratelimiter.cache;


@FunctionalInterface
public interface CacheLoader<K, V> {

    V load(K key) throws Exception;

}
