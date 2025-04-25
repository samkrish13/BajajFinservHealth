package com.example.webhookapp.app.service;

import com.example.webhookapp.app.models.User;
import com.example.webhookapp.app.utils.RetryHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Component
public class StartupRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";

    @Override
    public void run(String... args) throws Exception {
        // Step 1: Send request to generateWebhook
        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("name", "John Doe");
        requestPayload.put("regNo", "REG12347");
        requestPayload.put("email", "john@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestPayload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Failed to fetch webhook.");
            return;
        }

        // Extract response
        Map body = response.getBody();
        String webhookUrl = (String) body.get("webhook");
        String accessToken = (String) body.get("accessToken");
        Map data = (Map) body.get("data");

        // Check which question to solve based on regNo (last two digits)
        String regNo = requestPayload.get("regNo");
        int lastTwoDigits = Integer.parseInt(regNo.replaceAll("\\D", "").substring(regNo.length() - 2));
        boolean isOdd = lastTwoDigits % 2 == 1;

        List<List<Integer>> result = isOdd
                ? solveMutualFollowers((List<Map<String, Object>>) data.get("users"))
                : solveNthLevel((Map<String, Object>) data); // You can expand this if you need to support Question 2

        // Prepare final payload
        Map<String, Object> finalPayload = new HashMap<>();
        finalPayload.put("regNo", regNo);
        finalPayload.put("outcome", result);

        // Step 3: Send result to webhook with retry
        RetryHandler retryHandler = new RetryHandler(restTemplate);
        retryHandler.sendWithRetry(webhookUrl, accessToken, finalPayload, 4);
    }

    // Question 1: Mutual Followers
    private List<List<Integer>> solveMutualFollowers(List<Map<String, Object>> usersData) {
        Map<Integer, Set<Integer>> followsMap = new HashMap<>();
        for (Map<String, Object> user : usersData) {
            Integer id = (Integer) user.get("id");
            List<Integer> followsList = (List<Integer>) user.get("follows");
            followsMap.put(id, new HashSet<>(followsList));
        }

        Set<String> visited = new HashSet<>();
        List<List<Integer>> result = new ArrayList<>();

        for (Integer userId : followsMap.keySet()) {
            for (Integer followId : followsMap.get(userId)) {
                if (followsMap.containsKey(followId) && followsMap.get(followId).contains(userId)) {
                    int min = Math.min(userId, followId);
                    int max = Math.max(userId, followId);
                    String key = min + "-" + max;
                    if (!visited.contains(key)) {
                        visited.add(key);
                        result.add(Arrays.asList(min, max));
                    }
                }
            }
        }
        return result;
    }

    // (Optional) Question 2: N-th Level Followers
    private List<List<Integer>> solveNthLevel(Map<String, Object> data) {
        // You can implement this if your REG number is even
        return List.of(); // Placeholder
    }
}
