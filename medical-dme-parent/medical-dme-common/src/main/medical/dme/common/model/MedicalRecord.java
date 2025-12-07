package com.medical.dme.common.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicalRecord {
    @XmlAttribute
    private String recordId;

    @XmlAttribute
    private LocalDateTime creationDate;

    @XmlElement
    private Patient patient;

    @XmlElementWrapper(name = "consultations")
    @XmlElement(name = "consultation")
    private List<String> consultations;

    @XmlElementWrapper(name = "prescriptions")
    @XmlElement(name = "prescription")
    private List<String> prescriptions;
}
