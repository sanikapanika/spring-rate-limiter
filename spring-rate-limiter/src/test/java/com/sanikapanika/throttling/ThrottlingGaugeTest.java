package com.sanikapanika.ratelimiter;

import org.junit.Test;
import org.springframework.util.Assert;

public class ThrottlingGaugeTest {

    @Test
    public void testThrottlingGauge() throws InterruptedException {
        ThrottlingGauge gauge = new ThrottlingGauge(1000L, 1);

        gauge.removeEldest();
        Assert.isTrue(gauge.throttle(), "Should be ok with the first call");

        gauge.removeEldest();
        Assert.isTrue(!gauge.throttle(), "Shouldn't be ok with the next call");

        gauge.removeEldest();
        Assert.isTrue(!gauge.throttle(), "Shouldn't be ok with the next call");

        gauge.removeEldest();
        Assert.isTrue(!gauge.throttle(), "Shouldn't be ok with the next call");

        Thread.sleep(1100);

        gauge.removeEldest();
        Assert.isTrue(gauge.throttle(), "Should be ok with the call after sleep 1 sec.");
    }

}