package com.example.webhookapp.utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RetryHandler {

    private final RestTemplate restTemplate;

    public RetryHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendWithRetry(String url, String token, Map<String, Object> payload, int maxAttempts) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Successfully posted to webhook on attempt " + attempt);
                    break;
                } else {
                    System.err.println("Attempt " + attempt + " failed with status: " + response.getStatusCode());
                }
            } catch (Exception ex) {
                System.err.println("Attempt " + attempt + " failed: " + ex.getMessage());
            }
        }
    }
}
