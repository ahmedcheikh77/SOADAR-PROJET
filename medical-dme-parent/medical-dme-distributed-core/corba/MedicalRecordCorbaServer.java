package com.medical.dme.distributed.corba;

import MedicalDME.Corba.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class MedicalRecordCorbaServer extends MedicalRecordServicePOA {

    private ORB orb;
    private org.omg.CORBA.Object ref;
    private org.omg.CORBA.Object objRef;
    private POA rootpoa;

    @PostConstruct
    public void start() {
        try {
            // Initialiser l'ORB
            String[] args = {"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};
            orb = ORB.init(args, null);

            // Obtenir la référence au POA racine
            objRef = orb.resolve_initial_references("RootPOA");
            rootpoa = POAHelper.narrow(objRef);

            // Activer le POA Manager
            rootpoa.the_POAManager().activate();

            // Créer le servant
            MedicalRecordService servant = new MedicalRecordCorbaServer();

            // Obtenir la référence de l'objet
            ref = rootpoa.servant_to_reference(servant);

            // Obtenir le contexte de nommage racine
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // Lier l'objet dans le service de nommage
            String name = "MedicalRecordService";
            NameComponent[] path = ncRef.to_name(name);
            ncRef.rebind(path, ref);

            System.out.println("CORBA Server ready and waiting...");

            // Lancer l'ORB dans un thread séparé
            new Thread(() -> {
                orb.run();
            }).start();

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        if (orb != null) {
            try {
                orb.shutdown(true);
                orb.destroy();
                System.out.println("CORBA Server stopped");
            } catch (Exception e) {
                System.err.println("Error stopping CORBA server: " + e.getMessage());
            }
        }
    }

    @Override
    public MedicalRecord getMedicalRecord(String patientId) {
        System.out.println("CORBA Request received for patient: " + patientId);

        // Créer un enregistrement médical de test
        Patient patient = new Patient(
                patientId,
                "John",
                "Doe",
                "1980-05-15",
                "MALE"
        );

        Consultation[] consultations = new Consultation[1];
        consultations[0] = new Consultation(
                "CONS001",
                "2024-01-15T10:30:00",
                "DOC001",
                "Fever, cough",
                "Common cold"
        );

        Prescription[] prescriptions = new Prescription[1];
        prescriptions[0] = new Prescription(
                "PRES001",
                "Paracetamol",
                "500mg",
                "Every 6 hours",
                "2024-01-15",
                "2024-01-20"
        );

        return new MedicalRecord(
                "REC001",
                patient,
                consultations,
                prescriptions
        );
    }

    @Override
    public boolean updateMedicalRecord(MedicalRecord record) {
        System.out.println("CORBA Update received for patient: " +
                record.patient.patientId);
        return true;
    }

    @Override
    public String addConsultation(String patientId, Consultation consultation) {
        System.out.println("CORBA Add consultation for patient: " + patientId);
        return "CONS_" + System.currentTimeMillis();
    }

    @Override
    public String[] getAllPatientIds() {
        return new String[]{"PAT001", "PAT002", "PAT003"};
    }
}
