package com.rateLimiter.service;

import org.springframework.stereotype.Service;
import java.util.logging.Logger;

@Service
public class RateLimiterService {

    private static final Logger logger = Logger.getLogger(RateLimiterService.class.getName());

    public String someApi(String userId) {
        // Log the request for tracking
        logger.info("Processing request for user: " + userId);

        // Simulate some processing logic
        // You can add actual business logic here
        return "Request processed successfully for user: " + userId;
    }
}
