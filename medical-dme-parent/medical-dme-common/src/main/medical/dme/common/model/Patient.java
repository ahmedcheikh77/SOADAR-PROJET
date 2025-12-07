package com.medical.dme.common.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Patient {
    @XmlElement
    private String patientId;

    @XmlElement
    private String firstName;

    @XmlElement
    private String lastName;

    @XmlElement
    private LocalDate dateOfBirth;

    @XmlElement
    private Gender gender;

    @XmlElement
    private Contact contact;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}