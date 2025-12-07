package com.medical.dme.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public class MedicalRecordSOAPService {

    @WebMethod
    public String getMedicalRecord(String patientId) {
        return "SOAP Response: Medical record for patient " + patientId +
                " - Name: John Doe, DOB: 1980-05-15";
    }

    @WebMethod
    public String createMedicalRecord(String patientData) {
        return "SOAP Response: Medical record created successfully - " + patientData;
    }
}