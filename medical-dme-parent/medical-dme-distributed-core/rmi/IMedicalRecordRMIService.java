package com.medical.dme.distributed.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MedicalRecordRMIService extends Remote {
    String getMedicalRecord(String patientId) throws RemoteException;
    String syncMedicalRecord(String recordData) throws RemoteException;
}