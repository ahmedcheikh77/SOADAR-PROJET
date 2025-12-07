#!/usr/bin/env python3
"""
Client Python pour le service CORBA du système DME
Démontre l'interopérabilité multi-langage
"""

import sys
import CosNaming
from omniORB import CORBA
import MedicalDME.Corba

class MedicalRecordClient:
    def __init__(self, host="localhost", port=1050):
        """Initialiser le client CORBA"""
        self.host = host
        self.port = port
        self.orb = None
        self.service = None

    def connect(self):
        """Se connecter au serveur CORBA"""
        try:
            # Initialiser l'ORB
            args = ["-ORBInitRef",
                    f"NameService=corbaname::#{self.host}:{self.port}"]
            self.orb = CORBA.ORB_init(args, CORBA.ORB_ID)

            # Obtenir la référence au service de nommage
            obj = self.orb.resolve_initial_references("NameService")
            rootContext = obj._narrow(CosNaming.NamingContext)

            if rootContext is None:
                print("Failed to narrow the root naming context")
                return False

            # Résoudre le nom du service
            name = [CosNaming.NameComponent("MedicalRecordService", "")]
            obj = rootContext.resolve(name)

            # Obtenir la référence du service
            self.service = obj._narrow(MedicalDME.Corba.MedicalRecordService)

            if self.service is None:
                print("Failed to narrow the MedicalRecordService")
                return False

            print("Successfully connected to CORBA Medical Record Service")
            return True

        except Exception as e:
            print(f"Connection failed: {e}")
            return False

    def get_medical_record(self, patient_id):
        """Récupérer un dossier médical"""
        try:
            print(f"Requesting medical record for patient: {patient_id}")
            record = self.service.getMedicalRecord(patient_id)

            print(f"\n=== Medical Record ===")
            print(f"Record ID: {record.recordId}")
            print(f"Patient: {record.patient.firstName} {record.patient.lastName}")
            print(f"Date of Birth: {record.patient.dateOfBirth}")
            print(f"Gender: {record.patient.gender}")

            print(f"\nConsultations:")
            for consultation in record.consultations:
                print(f"  - {consultation.date}: {consultation.diagnosis}")

            print(f"\nPrescriptions:")
            for prescription in record.prescriptions:
                print(f"  - {prescription.medication}: {prescription.dosage}")

            return record

        except Exception as e:
            print(f"Error retrieving record: {e}")
            return None

    def update_medical_record(self, record):
        """Mettre à jour un dossier médical"""
        try:
            result = self.service.updateMedicalRecord(record)
            print(f"Update successful: {result}")
            return result
        except Exception as e:
            print(f"Error updating record: {e}")
            return False

    def add_consultation(self, patient_id, symptoms, diagnosis):
        """Ajouter une consultation"""
        try:
            consultation = MedicalDME.Corba.Consultation()
            consultation.consultationId = f"CONS_{int(time.time())}"
            consultation.date = datetime.now().isoformat()
            consultation.doctorId = "PYTHON_DOCTOR"
            consultation.symptoms = symptoms
            consultation.diagnosis = diagnosis

            result = self.service.addConsultation(patient_id, consultation)
            print(f"Consultation added with ID: {result}")
            return result

        except Exception as e:
            print(f"Error adding consultation: {e}")
            return None

    def get_all_patients(self):
        """Obtenir tous les IDs patients"""
        try:
            patient_ids = self.service.getAllPatientIds()
            print(f"\n=== All Patients ===")
            for pid in patient_ids:
                print(f"  - {pid}")
            return patient_ids
        except Exception as e:
            print(f"Error getting patients: {e}")
            return []

def main():
    """Fonction principale"""
    client = MedicalRecordClient()

    if not client.connect():
        print("Failed to connect to CORBA service")
        sys.exit(1)

    # Tester les différentes opérations
    print("\n1. Getting all patients...")
    client.get_all_patients()

    print("\n2. Getting specific medical record...")
    client.get_medical_record("PAT001")

    print("\n3. Adding a consultation...")
    client.add_consultation("PAT001", "Headache, nausea", "Migraine")

    print("\nCORBA Python Client demonstration completed!")

if __name__ == "__main__":
    main()