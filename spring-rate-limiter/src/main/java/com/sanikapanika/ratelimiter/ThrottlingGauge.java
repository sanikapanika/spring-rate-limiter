package com.sanikapanika.ratelimiter;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThrottlingGauge {

    private final int throttleLimit;
    private final long mills;
    private final ArrayList<Long> callTimestamps;
    private final ReadWriteLock lock;

    public ThrottlingGauge(Long mills, int throttleLimit) {
        this.throttleLimit = throttleLimit;
        this.mills = mills;
        this.callTimestamps = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock(true);
    }

    public boolean throttle() {
        lock.readLock().lock();
        try {
            if (callTimestamps.size() >= throttleLimit) {
                return false;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            if (callTimestamps.size() < throttleLimit) {
                callTimestamps.add(System.currentTimeMillis());
                return true;
            } else {
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeEldest() {
        long threshold = System.currentTimeMillis() - this.mills;
        lock.writeLock().lock();
        try {
            callTimestamps.removeIf(it -> it < threshold);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
