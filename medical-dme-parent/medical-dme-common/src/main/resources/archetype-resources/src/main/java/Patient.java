package com.medical.dme.common.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.time.LocalDate;

@XmlRootElement(name = "patient")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @XmlElement(required = true)
    private String patientId;

    @XmlElement(required = true)
    private String firstName;

    @XmlElement(required = true)
    private String lastName;

    @XmlElement(required = true)
    private LocalDate dateOfBirth;

    @XmlElement(required = true)
    private Gender gender;

    @XmlElement(required = true)
    private Contact contact;

    private Contact emergencyContact;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}