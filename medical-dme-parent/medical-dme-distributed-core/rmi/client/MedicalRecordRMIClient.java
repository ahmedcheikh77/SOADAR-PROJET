package com.medical.dme.distributed.rmi.client;

import com.medical.dme.distributed.rmi.IMedicalRecordRMIService;
import com.medical.dme.common.model.MedicalRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Component
public class MedicalRecordRMIClient {

    @Value("${rmi.registry.host:localhost}")
    private String registryHost;

    @Value("${rmi.registry.port:1099}")
    private int registryPort;

    @Value("${rmi.server.name:MedicalRecordService}")
    private String serviceName;

    private IMedicalRecordRMIService rmiService;

    @PostConstruct
    public void init() {
        connectToRMIServer();
    }

    private void connectToRMIServer() {
        try {
            System.out.println("Connecting to RMI registry at " +
                    registryHost + ":" + registryPort);

            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            rmiService = (IMedicalRecordRMIService) registry.lookup(serviceName);

            // Tester la connexion
            if (rmiService.isNodeAvailable()) {
                System.out.println("Successfully connected to RMI service");
                System.out.println("Node Info: " + rmiService.getNodeInfo());

                // S'enregistrer comme nœud client
                registerAsNode();
            }

        } catch (Exception e) {
            System.err.println("Failed to connect to RMI server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerAsNode() {
        try {
            String clientNodeId = "RMI_Client_" + System.currentTimeMillis();
            String clientEndpoint = "client://" + System.getProperty("user.name");

            rmiService.registerNode(clientNodeId, clientEndpoint);
            System.out.println("Registered as node: " + clientNodeId);

        } catch (Exception e) {
            System.err.println("Failed to register node: " + e.getMessage());
        }
    }

    public MedicalRecord getMedicalRecord(String patientId) {
        try {
            return rmiService.getMedicalRecord(patientId);
        } catch (Exception e) {
            System.err.println("RMI call failed: " + e.getMessage());
            return null;
        }
    }

    public void syncMedicalRecord(MedicalRecord record) {
        try {
            rmiService.syncMedicalRecord(record);
            System.out.println("Record synced successfully via RMI");
        } catch (Exception e) {
            System.err.println("Failed to sync record via RMI: " + e.getMessage());
        }
    }
}
