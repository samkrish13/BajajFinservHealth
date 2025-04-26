package com.example.webhookapp.app.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

    public String processWebhook(String requestBody) {
        // Initialize ObjectMapper for JSON processing
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Deserialize the requestBody into JsonNode
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            // Extract necessary data from JSON (for example, regNo)
            String regNo = jsonNode.path("regNo").asText();

            // Logic to determine which question to handle based on the regNo
            int lastDigit = Integer.parseInt(regNo.substring(regNo.length() - 1));

            // Use the extracted 'regNo' and perform different actions based on its last digit
            if (lastDigit % 2 == 0) {
                // Handle Nth-Level Followers (Question 2)
                return handleNthLevelFollowers(jsonNode);
            } else {
                // Handle Mutual Followers (Question 1)
                return handleMutualFollowers(jsonNode);
            }

        } catch (JsonProcessingException e) {
            // Handle JSON parsing or mapping exceptions
            e.printStackTrace();
            return "Error processing JSON";
        } catch (Exception e) {
            // Handle other generic exceptions
            e.printStackTrace();
            return "Unknown error occurred";
        }
    }

    // Logic for handling Mutual Followers (Question 1)
    private String handleMutualFollowers(JsonNode usersNode) {
        // Your logic for identifying mutual follow pairs goes here
        // You would process the usersNode to find mutual followers
        // For the sake of example, let's return a placeholder response
        return "Handled Mutual Followers Logic";
    }

    // Logic for handling Nth-Level Followers (Question 2)
    private String handleNthLevelFollowers(JsonNode usersNode) {
        // Your logic for identifying nth-level followers goes here
        // For example, you can process the usersNode to find followers at a specific level
        // For the sake of example, let's return a placeholder response
        return "Handled Nth-Level Followers Logic";
    }
}
