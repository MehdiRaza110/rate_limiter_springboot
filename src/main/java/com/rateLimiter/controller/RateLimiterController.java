package com.rateLimiter.controller;

import com.rateLimiter.exception.RateLimitExceededException;
import com.rateLimiter.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RateLimiterController {

    @Autowired
    private RateLimiterService rateLimiterService;

    @GetMapping("/api/{userId}")
    public ResponseEntity<Map<String, Object>> callApi(@PathVariable String userId) {
        try {
            String message = rateLimiterService.someApi(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", message);
            response.put("status", HttpStatus.OK.value());
            response.put("timestamp", LocalDateTime.now());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RateLimitExceededException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            errorResponse.put("timestamp", LocalDateTime.now());

            return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
