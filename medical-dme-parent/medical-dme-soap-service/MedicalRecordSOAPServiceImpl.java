package com.medical.dme.soap.impl;

import com.medical.dme.common.model.*;
import com.medical.dme.soap.IMedicalRecordSOAPService;
import jakarta.jws.WebService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebService(
        serviceName = "MedicalRecordService",
        portName = "MedicalRecordServicePort",
        targetNamespace = "http://medical.dme.com/soap",
        endpointInterface = "com.medical.dme.soap.IMedicalRecordSOAPService"
)
public class MedicalRecordSOAPServiceImpl implements IMedicalRecordSOAPService {

    private final Map<String, MedicalRecord> medicalRecords = new ConcurrentHashMap<>();

    public MedicalRecordSOAPServiceImpl() {
        loadSampleData();
    }

    @Override
    public MedicalRecord getMedicalRecord(String patientId) {
        System.out.println("SOAP Request received for patient: " + patientId);

        MedicalRecord record = medicalRecords.get(patientId);
        if (record == null) {
            throw new RuntimeException("Medical record not found for patient: " + patientId);
        }

        return record;
    }

    @Override
    public String createMedicalRecord(MedicalRecord medicalRecord) {
        String patientId = medicalRecord.getPatient().getPatientId();

        if (medicalRecords.containsKey(patientId)) {
            throw new RuntimeException("Medical record already exists for patient: " + patientId);
        }

        medicalRecord.setCreationDate(LocalDateTime.now());
        medicalRecord.setRecordId("REC-" + UUID.randomUUID().toString().substring(0, 8));

        medicalRecords.put(patientId, medicalRecord);

        System.out.println("Medical record created for patient: " + patientId);
        return medicalRecord.getRecordId();
    }

    @Override
    public boolean updateMedicalRecord(MedicalRecord medicalRecord) {
        String patientId = medicalRecord.getPatient().getPatientId();

        if (!medicalRecords.containsKey(patientId)) {
            throw new RuntimeException("Medical record not found for patient: " + patientId);
        }

        medicalRecord.setCreationDate(LocalDateTime.now());
        medicalRecords.put(patientId, medicalRecord);

        System.out.println("Medical record updated for patient: " + patientId);
        return true;
    }

    @Override
    public boolean deleteMedicalRecord(String patientId) {
        if (!medicalRecords.containsKey(patientId)) {
            throw new RuntimeException("Medical record not found for patient: " + patientId);
        }

        medicalRecords.remove(patientId);
        System.out.println("Medical record deleted for patient: " + patientId);
        return true;
    }

    private void loadSampleData() {
        Patient patient = new Patient();
        patient.setPatientId("PAT001");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth(LocalDate.of(1980, 5, 15));
        patient.setGender(Patient.Gender.MALE);

        Contact contact = new Contact();
        contact.setEmail("john.doe@email.com");
        contact.setPhone("123-456-7890");

        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setPostalCode("10001");
        address.setCountry("USA");
        contact.setAddress(address);

        patient.setContact(contact);

        MedicalRecord record = new MedicalRecord();
        record.setRecordId("REC-001");
        record.setCreationDate(LocalDateTime.now());
        record.setPatient(patient);
        record.setConsultations(new ArrayList<>());
        record.setPrescriptions(new ArrayList<>());
        record.setAllergies(new ArrayList<>());

        medicalRecords.put("PAT001", record);
        System.out.println("Sample data loaded for patient PAT001");
    }
}