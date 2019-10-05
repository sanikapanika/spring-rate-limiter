package com.sanikapanika.ratelimiter.service;

import com.sanikapanika.ratelimiter.Throttling;

import java.lang.reflect.Method;


public interface ThrottlingEvaluator {

    String evaluate(Throttling throttlingConfig, Object bean, Class clazz, Method method, Object[] args);

}
