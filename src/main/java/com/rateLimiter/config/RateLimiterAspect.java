package com.rateLimiter.config;

import com.rateLimiter.exception.RateLimitExceededException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class RateLimiterAspect {

    private final Map<String, Bucket> userBuckets = new HashMap<>();

    @Before("execution(* com.rateLimiter.service.RateLimiterService.someApi(..)) && args(userId)")
    public void rateLimit(String userId) throws RateLimitExceededException {
        // Limit to 5 requests every 1 minute
        Bucket bucket = userBuckets.computeIfAbsent(userId, k -> new Bucket(5, 1, TimeUnit.MINUTES));
        if (!bucket.allowRequest()) {
            throw new RateLimitExceededException("Rate limit exceeded for user: " + userId);
        }
    }

    private static class Bucket {
        private final int maxTokens;  // Maximum requests allowed in the time frame
        private final Queue<Long> requestTimestamps; // Queue to store request timestamps

        public Bucket(int maxTokens, int refillRate, TimeUnit timeUnit) {
            this.maxTokens = maxTokens;
            this.requestTimestamps = new LinkedList<>();
        }

        public synchronized boolean allowRequest() {
            long currentTime = System.currentTimeMillis();
            long windowStart = currentTime - TimeUnit.MINUTES.toMillis(1); // 1 minute window

            // Remove timestamps outside the 1-minute window
            while (!requestTimestamps.isEmpty() && requestTimestamps.peek() < windowStart) {
                requestTimestamps.poll(); // Remove the oldest timestamp
            }

            // Check if we have room for another request
            if (requestTimestamps.size() < maxTokens) {
                requestTimestamps.offer(currentTime); // Add current request timestamp
                return true; // Request allowed
            }
            return false; // Rate limit exceeded
        }
    }
}
