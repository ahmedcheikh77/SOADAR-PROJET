package com.medical.dme.soap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SOAPApplication {
    public static void main(String[] args) {
        SpringApplication.run(SOAPApplication.class, args);
        System.out.println("=== Medical DME SOAP Service Started ===");
        System.out.println("WSDL: http://localhost:8081/medical/soap?wsdl");
    }
}