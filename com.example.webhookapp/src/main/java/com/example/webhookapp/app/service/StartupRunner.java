package com.example.webhookapp.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Your startup logic goes here
        System.out.println("Application has started!");
    }
}
