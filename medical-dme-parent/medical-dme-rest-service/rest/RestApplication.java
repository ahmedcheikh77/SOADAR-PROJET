package com.medical.dme.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
        System.out.println("=== Medical DME REST Service Started ===");
        System.out.println("URL: http://localhost:8082/api");
        System.out.println("H2 Console: http://localhost:8082/h2-console");
    }
}