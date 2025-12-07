package com.medical.dme.common.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@XmlRootElement(name = "medicalRecord", namespace = "http://medical.dme.com/records")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    @XmlAttribute(required = true)
    private String recordId;

    @XmlAttribute
    private LocalDateTime creationDate;

    @XmlElement(required = true)
    private Patient patient;

    @XmlElementWrapper(name = "consultations")
    @XmlElement(name = "consultation")
    private List<Consultation> consultations;

    @XmlElementWrapper(name = "prescriptions")
    @XmlElement(name = "prescription")
    private List<Prescription> prescriptions;

    @XmlElementWrapper(name = "allergies")
    @XmlElement(name = "allergy")
    private List<Allergy> allergies;
}