package com.medical.dme.rest.service.impl;

import com.medical.dme.rest.dto.*;
import com.medical.dme.rest.entity.*;
import com.medical.dme.rest.repository.MedicalRecordRepository;
import com.medical.dme.rest.repository.PatientRepository;
import com.medical.dme.rest.service.MedicalRecordService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Page<MedicalRecordDTO> getAllRecords(Pageable pageable) {
        return medicalRecordRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public MedicalRecordDTO getByPatientId(String patientId) {
        MedicalRecordEntity record = medicalRecordRepository.findByPatientPatientId(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found for patient: " + patientId));
        return convertToDTO(record);
    }

    @Override
    public MedicalRecordDTO createRecord(MedicalRecordDTO recordDTO) {
        // Vérifier si le patient existe
        PatientEntity patient = patientRepository.findById(recordDTO.getPatientId())
                .orElseGet(() -> createPatientFromDTO(recordDTO));

        MedicalRecordEntity record = convertToEntity(recordDTO);
        record.setPatient(patient);

        MedicalRecordEntity savedRecord = medicalRecordRepository.save(record);

        // Synchroniser avec MongoDB pour les recherches full-text
        syncToMongoDB(savedRecord);

        return convertToDTO(savedRecord);
    }

    @Override
    public MedicalRecordDTO updateRecord(String recordId, MedicalRecordDTO recordDTO) {
        MedicalRecordEntity existingRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found: " + recordId));

        // Mettre à jour les champs
        updateEntityFromDTO(existingRecord, recordDTO);

        MedicalRecordEntity updatedRecord = medicalRecordRepository.save(existingRecord);

        // Journaliser la mise à jour pour l'audit
        logUpdate(recordId, "UPDATE_RECORD");

        return convertToDTO(updatedRecord);
    }

    @Override
    public void deleteRecord(String recordId) {
        MedicalRecordEntity record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found: " + recordId));

        // Archiver avant suppression (soft delete)
        archiveRecord(record);

        medicalRecordRepository.delete(record);

        logUpdate(recordId, "DELETE_RECORD");
    }

    @Override
    public ConsultationDTO addConsultation(String recordId, ConsultationDTO consultationDTO) {
        MedicalRecordEntity record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found: " + recordId));

        ConsultationEntity consultation = convertToConsultationEntity(consultationDTO);
        consultation.setRecord(record);

        record.getConsultations().add(consultation);
        medicalRecordRepository.save(record);

        return convertToConsultationDTO(consultation);
    }

    @Override
    public PrescriptionDTO addPrescription(String recordId, PrescriptionDTO prescriptionDTO) {
        MedicalRecordEntity record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found: " + recordId));

        // Valider la prescription
        validatePrescription(prescriptionDTO, record);

        PrescriptionEntity prescription = convertToPrescriptionEntity(prescriptionDTO);
        prescription.setRecord(record);

        record.getPrescriptions().add(prescription);
        medicalRecordRepository.save(record);

        return convertToPrescriptionDTO(prescription);
    }

    @Override
    public List<MedicalRecordDTO> findByConsultationDateBetween(LocalDate startDate, LocalDate endDate) {
        return medicalRecordRepository.findByConsultationDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO> getActivePrescriptions(String recordId) {
        MedicalRecordEntity record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found: " + recordId));

        return record.getPrescriptions().stream()
                .filter(p -> p.getEndDate() == null || p.getEndDate().isAfter(LocalDate.now()))
                .map(this::convertToPrescriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MedicalStatsDTO getMedicalStatistics() {
        MedicalStatsDTO stats = new MedicalStatsDTO();

        stats.setTotalPatients(patientRepository.count());
        stats.setTotalRecords(medicalRecordRepository.count());
        stats.setAverageConsultationsPerRecord(medicalRecordRepository.getAverageConsultations());
        stats.setMostCommonAllergy(medicalRecordRepository.getMostCommonAllergy());

        return stats;
    }

    private void validatePrescription(PrescriptionDTO prescription, MedicalRecordEntity record) {
        // Vérifier les allergies
        boolean hasAllergy = record.getAllergies().stream()
                .anyMatch(a -> a.getSubstance().equalsIgnoreCase(prescription.getMedication()));

        if (hasAllergy) {
            throw new IllegalArgumentException("Patient is allergic to " + prescription.getMedication());
        }

        // Vérifier les interactions médicamenteuses
        List<String> currentMeds = record.getPrescriptions().stream()
                .map(PrescriptionEntity::getMedication)
                .collect(Collectors.toList());

        if (hasDrugInteraction(prescription.getMedication(), currentMeds)) {
            throw new IllegalArgumentException("Drug interaction detected with " + prescription.getMedication());
        }
    }

    private void syncToMongoDB(MedicalRecordEntity record) {
        // Synchroniser avec MongoDB pour les recherches
        // Implémentation à compléter
    }

    private void archiveRecord(MedicalRecordEntity record) {
        // Archiver le dossier avant suppression
        // Implémentation à compléter
    }

    private void logUpdate(String recordId, String action) {
        // Journaliser pour l'audit
        System.out.println("[" + LocalDate.now() + "] " + action + " on record: " + recordId);
    }

    // Méthodes de conversion DTO/Entity
    private MedicalRecordDTO convertToDTO(MedicalRecordEntity entity) {
        return modelMapper.map(entity, MedicalRecordDTO.class);
    }

    private MedicalRecordEntity convertToEntity(MedicalRecordDTO dto) {
        return modelMapper.map(dto, MedicalRecordEntity.class);
    }

    // Autres méthodes de conversion...
}