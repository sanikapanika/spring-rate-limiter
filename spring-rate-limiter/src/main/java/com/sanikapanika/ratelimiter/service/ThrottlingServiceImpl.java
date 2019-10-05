package com.sanikapanika.ratelimiter.service;

import com.sanikapanika.ratelimiter.ThrottlingGauge;
import com.sanikapanika.ratelimiter.ThrottlingKey;
import com.sanikapanika.ratelimiter.cache.Cache;
import com.sanikapanika.ratelimiter.cache.CacheBuilder;
import com.sanikapanika.ratelimiter.cache.CacheLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutionException;


public class ThrottlingServiceImpl implements ThrottlingService {

    private final Log logger = LogFactory.getLog(getClass());

    private final Cache<ThrottlingKey, ThrottlingGauge> cache;
    private final CacheLoader<ThrottlingKey, ThrottlingGauge> gaugeLoader = key -> new ThrottlingGauge(key.getTimeUnit(), key.getLimit());


    public ThrottlingServiceImpl(int cacheSize) {
        this.cache = CacheBuilder.<ThrottlingKey, ThrottlingGauge>builder()
                .setMaximumWeight(cacheSize)
                .build();
    }

    @Override
    public boolean throttle(ThrottlingKey key, String evaluatedValue) {

        try {

            ThrottlingGauge gauge = cache.computeIfAbsent(key, gaugeLoader);
            gauge.removeEldest();
            return gauge.throttle();

        } catch (ExecutionException e) {
            if (logger.isErrorEnabled()) {
                logger.error("exception occurred while calculating throttle value", e);
            }
        }

        return true;
    }

}
