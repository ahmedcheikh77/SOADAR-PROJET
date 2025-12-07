package com.medical.dme.soap;

import com.medical.dme.soap.impl.MedicalRecordSOAPServiceImpl;
import jakarta.xml.ws.Endpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SOAPServicePublisher {

    public static void main(String[] args) {
        SpringApplication.run(SOAPServicePublisher.class, args);
    }

    @Bean
    public Endpoint publishSOAPService() {
        MedicalRecordSOAPServiceImpl service = new MedicalRecordSOAPServiceImpl();
        String address = "http://localhost:8081/medical/soap/recordService";

        Endpoint endpoint = Endpoint.publish(address, service);

        // Configuration WS-Security
        Map<String, Object> props = new HashMap<>();
        props.put(Endpoint.WSDL_SERVICE, "MedicalRecordService");
        props.put(Endpoint.WSDL_PORT, "MedicalRecordServicePort");

        return endpoint;
    }

    @Bean
    public jakarta.xml.ws.handler.Handler securityHandler() {
        return new jakarta.xml.ws.handler.soap.SOAPHandler<jakarta.xml.ws.handler.soap.SOAPMessageContext>() {
            @Override
            public boolean handleMessage(SOAPMessageContext context) {
                // Implémentation de la sécurité WS-Security
                return true;
            }

            // Autres méthodes obligatoires
        };
    }
}