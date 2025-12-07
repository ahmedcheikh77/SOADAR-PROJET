package com.medical.dme.rest.controller;

import com.medical.dme.common.model.MedicalRecord;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final Map<String, MedicalRecord> records = new HashMap<>();

    public MedicalRecordController() {
        // Données de test
        MedicalRecord testRecord = new MedicalRecord();
        testRecord.setRecordId("REC-001");
        testRecord.setPatient(new com.medical.dme.common.model.Patient(
                "PAT001", "John", "Doe",
                java.time.LocalDate.of(1980, 5, 15),
                com.medical.dme.common.model.Patient.Gender.MALE,
                null
        ));
        records.put("PAT001", testRecord);
    }

    @GetMapping
    public List<MedicalRecord> getAllRecords() {
        return new ArrayList<>(records.values());
    }

    @GetMapping("/{patientId}")
    public MedicalRecord getRecord(@PathVariable String patientId) {
        MedicalRecord record = records.get(patientId);
        if (record == null) {
            throw new RuntimeException("Medical record not found for patient: " + patientId);
        }
        return record;
    }

    @PostMapping
    public MedicalRecord createRecord(@RequestBody MedicalRecord record) {
        String patientId = record.getPatient().getPatientId();
        record.setRecordId("REC-" + UUID.randomUUID().toString().substring(0, 8));
        record.setCreationDate(java.time.LocalDateTime.now());
        records.put(patientId, record);
        return record;
    }

    @PutMapping("/{patientId}")
    public MedicalRecord updateRecord(@PathVariable String patientId,
                                      @RequestBody MedicalRecord record) {
        if (!records.containsKey(patientId)) {
            throw new RuntimeException("Medical record not found for patient: " + patientId);
        }
        records.put(patientId, record);
        return record;
    }

    @DeleteMapping("/{patientId}")
    public String deleteRecord(@PathVariable String patientId) {
        if (!records.containsKey(patientId)) {
            throw new RuntimeException("Medical record not found for patient: " + patientId);
        }
        records.remove(patientId);
        return "Medical record deleted successfully for patient: " + patientId;
    }
}