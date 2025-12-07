package com.medical.dme.rest.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final Map<String, Map<String, Object>> records = new HashMap<>();

    public MedicalRecordController() {
        // Données de test
        Map<String, Object> testRecord = new HashMap<>();
        testRecord.put("patientId", "PAT001");
        testRecord.put("firstName", "John");
        testRecord.put("lastName", "Doe");
        testRecord.put("dateOfBirth", "1980-05-15");
        testRecord.put("gender", "MALE");
        records.put("PAT001", testRecord);
    }

    @GetMapping
    public List<Map<String, Object>> getAllRecords() {
        return new ArrayList<>(records.values());
    }

    @GetMapping("/{patientId}")
    public Map<String, Object> getRecord(@PathVariable String patientId) {
        if (!records.containsKey(patientId)) {
            throw new RuntimeException("Record not found for patient: " + patientId);
        }
        return records.get(patientId);
    }

    @PostMapping
    public Map<String, Object> createRecord(@RequestBody Map<String, Object> record) {
        String patientId = (String) record.get("patientId");
        records.put(patientId, record);
        return record;
    }

    @PutMapping("/{patientId}")
    public Map<String, Object> updateRecord(@PathVariable String patientId,
                                            @RequestBody Map<String, Object> record) {
        if (!records.containsKey(patientId)) {
            throw new RuntimeException("Record not found for patient: " + patientId);
        }
        records.put(patientId, record);
        return record;
    }

    @DeleteMapping("/{patientId}")
    public String deleteRecord(@PathVariable String patientId) {
        if (!records.containsKey(patientId)) {
            throw new RuntimeException("Record not found for patient: " + patientId);
        }
        records.remove(patientId);
        return "Record deleted successfully for patient: " + patientId;
    }
}