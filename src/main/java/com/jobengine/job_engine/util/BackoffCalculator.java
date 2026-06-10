package com.jobengine.job_engine.util;

import java.time.Instant;

public class BackoffCalculator {

    private static final long BASE_DELAY_MS= 2000L;

    private static final long MAX_DELAY_MS= 60000L;

    public static Instant nextRetryAt(int attempt){

        long delayMs= Math.min(
                (long) Math.pow(2,attempt) * BASE_DELAY_MS,
                MAX_DELAY_MS
        );

        return Instant.now().plusMillis(delayMs);
    }

    private BackoffCalculator(){}
}
