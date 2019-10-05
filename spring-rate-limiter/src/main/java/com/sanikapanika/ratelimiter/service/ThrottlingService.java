package com.sanikapanika.ratelimiter.service;

import com.sanikapanika.ratelimiter.ThrottlingKey;


public interface ThrottlingService {

    boolean throttle(ThrottlingKey key, String evaluatedValue);

}
