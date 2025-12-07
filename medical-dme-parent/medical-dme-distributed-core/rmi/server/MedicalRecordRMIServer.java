package com.medical.dme.distributed.rmi.server;

import com.medical.dme.distributed.rmi.IMedicalRecordRMIService;
import com.medical.dme.common.model.MedicalRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class MedicalRecordRMIServer extends UnicastRemoteObject
        implements IMedicalRecordRMIService {

    @Value("${rmi.registry.port:1099}")
    private int registryPort;

    @Value("${rmi.server.port:0}")
    private int serverPort;

    @Value("${rmi.server.name:MedicalRecordService}")
    private String serviceName;

    private Registry registry;
    private final Map<String, MedicalRecord> recordCache = new ConcurrentHashMap<>();
    private final Map<String, String> registeredNodes = new ConcurrentHashMap<>();

    public MedicalRecordRMIServer() throws RemoteException {
        super(); // Appel au constructeur de UnicastRemoteObject
    }

    @PostConstruct
    public void start() {
        try {
            // Créer ou obtenir le registre RMI
            registry = LocateRegistry.createRegistry(registryPort);

            // Enregistrer le service
            registry.rebind(serviceName, this);

            System.out.println("RMI Server started on port " + registryPort);
            System.out.println("Service registered as: " + serviceName);

            // Initialiser avec des données de test
            initializeSampleData();

        } catch (RemoteException e) {
            System.err.println("Failed to start RMI server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        try {
            // Désenregistrer le service
            if (registry != null) {
                registry.unbind(serviceName);
                System.out.println("RMI Service unregistered");
            }

            // Nettoyer
            UnicastRemoteObject.unexportObject(this, true);
            System.out.println("RMI Server stopped");

        } catch (Exception e) {
            System.err.println("Error stopping RMI server: " + e.getMessage());
        }
    }

    @Override
    public MedicalRecord getMedicalRecord(String patientId) throws RemoteException {
        System.out.println("RMI Request received for patient: " + patientId);

        MedicalRecord record = recordCache.get(patientId);
        if (record == null) {
            throw new RemoteException("Medical record not found for patient: " + patientId);
        }

        // Simuler un délai réseau
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return record;
    }

    @Override
    public void syncMedicalRecord(MedicalRecord record) throws RemoteException {
        String patientId = record.getPatient().getPatientId();

        System.out.println("Syncing medical record for patient: " + patientId);

        // Mettre à jour le cache
        recordCache.put(patientId, record);

        // Propager la synchronisation aux autres nœuds
        propagateSyncToOtherNodes(patientId, record);
    }

    @Override
    public boolean isNodeAvailable() throws RemoteException {
        return true;
    }

    @Override
    public String getNodeInfo() throws RemoteException {
        return "RMI Node - Port: " + registryPort +
                ", Registered Records: " + recordCache.size() +
                ", Connected Nodes: " + registeredNodes.size();
    }

    @Override
    public void registerNode(String nodeId, String endpoint) throws RemoteException {
        registeredNodes.put(nodeId, endpoint);
        System.out.println("Node registered: " + nodeId + " at " + endpoint);
    }

    @Override
    public void unregisterNode(String nodeId) throws RemoteException {
        registeredNodes.remove(nodeId);
        System.out.println("Node unregistered: " + nodeId);
    }

    private void propagateSyncToOtherNodes(String patientId, MedicalRecord record) {
        // Implémentation de la synchronisation multi-nœuds
        registeredNodes.forEach((nodeId, endpoint) -> {
            if (!nodeId.equals(this.serviceName)) {
                try {
                    // Se connecter au nœud distant et synchroniser
                    syncWithRemoteNode(endpoint, patientId, record);
                } catch (Exception e) {
                    System.err.println("Failed to sync with node " + nodeId + ": " + e.getMessage());
                }
            }
        });
    }

    private void syncWithRemoteNode(String endpoint, String patientId, MedicalRecord record) {
        // Implémenter la synchronisation avec le nœud distant
        // Cette méthode pourrait utiliser RMI, sockets ou HTTP
    }

    private void initializeSampleData() {
        // Initialiser avec des données de test
        // (Utiliser les mêmes classes que dans le module commun)
    }
}