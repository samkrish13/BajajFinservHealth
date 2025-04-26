package com.example.webhookapp.app.controller;

import com.example.webhookapp.app.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    @PostMapping("/generate")
    public String generateWebhook(@RequestBody String requestBody) {
        return webhookService.processWebhook(requestBody);
    }
}
