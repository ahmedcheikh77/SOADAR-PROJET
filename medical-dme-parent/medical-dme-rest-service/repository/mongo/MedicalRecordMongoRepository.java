package com.medical.dme.rest.repository.mongo;

import com.medical.dme.rest.entity.mongo.MedicalRecordDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalRecordMongoRepository extends MongoRepository<MedicalRecordDocument, String> {

    @Query("{ 'patient.patientId': ?0 }")
    MedicalRecordDocument findByPatientId(String patientId);

    @Query("{ 'patient.lastName': ?0 }")
    List<MedicalRecordDocument> findByLastName(String lastName);

    @Query("{ 'consultations.date': { $gte: ?0, $lte: ?1 } }")
    List<MedicalRecordDocument> findConsultationsBetweenDates(LocalDate start, LocalDate end);

    @Query("{ 'allergies.severity': 'SEVERE' }")
    List<MedicalRecordDocument> findPatientsWithSevereAllergies();

    // Recherche full-text
    @Query("{ $text: { $search: ?0 } }")
    List<MedicalRecordDocument> searchMedicalRecords(String searchText);
}