package com.skillsync.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {
    /**
     * Bootstraps and starts the Spring Boot application.
     *
     * @param args command-line arguments passed through to SpringApplication
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
