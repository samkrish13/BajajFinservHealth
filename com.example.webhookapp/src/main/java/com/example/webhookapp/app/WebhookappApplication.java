package com.example.webhookapp.app; // Adjust this package name based on your project setup

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class WebhookappApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebhookappApplication.class, args);
	}

	@Bean
	CommandLineRunner run(WebClient.Builder webClientBuilder) {
		return args -> {
			String requestBody = "{\"name\": \"John Doe\", \"regNo\": \"REG12347\", \"email\": \"john@example.com\"}";

			// WebClient setup to call generateWebhook endpoint
			WebClient client = webClientBuilder.baseUrl("https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook").build();
			String response = client.post()
					.header(HttpHeaders.CONTENT_TYPE, "application/json")
					.bodyValue(requestBody)
					.retrieve()
					.bodyToMono(String.class)
					.block();

			System.out.println("Response from generateWebhook: " + response);

			// Extract the webhook URL and access token
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(response);

			String webhookUrl = jsonNode.path("webhook").asText();
			String accessToken = jsonNode.path("accessToken").asText();
			JsonNode usersNode = jsonNode.path("data").path("users");

			// Logic to determine which question to handle based on the regNo
			String regNo = "REG12347";  // This can be dynamic, pulled from input
			int lastDigit = Integer.parseInt(regNo.substring(regNo.length() - 1));

			if (lastDigit % 2 == 0) {
				// Handle Nth-Level Followers (Question 2)
				handleNthLevelFollowers(usersNode, webhookUrl, accessToken);
			} else {
				// Handle Mutual Followers (Question 1)
				handleMutualFollowers(usersNode, webhookUrl, accessToken);
			}
		};
	}

	private void handleMutualFollowers(JsonNode usersNode, String webhookUrl, String accessToken) {
		// Implement logic for Question 1: Mutual Followers
		// Parse usersNode and find mutual follow pairs
	}

	private void handleNthLevelFollowers(JsonNode usersNode, String webhookUrl, String accessToken) {
		// Implement logic for Question 2: Nth-Level Followers
		// Parse usersNode and calculate nth-level followers
	}

	private void sendWebhookResponse(String webhookUrl, String accessToken, Object response) {
		WebClient client = WebClient.create(webhookUrl);
		client.post()
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.bodyValue(response)
				.retrieve()
				.bodyToMono(String.class)
				.doOnError(error -> System.out.println("Error sending to webhook: " + error.getMessage()))
				.block();
	}
}
