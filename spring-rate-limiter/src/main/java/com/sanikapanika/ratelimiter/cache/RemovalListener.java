package com.sanikapanika.ratelimiter.cache;


@FunctionalInterface
public interface RemovalListener<K, V> {

    void onRemoval(RemovalNotification<K, V> notification);

}
