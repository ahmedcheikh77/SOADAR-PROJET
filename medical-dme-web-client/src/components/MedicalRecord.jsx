import React, { useState, useEffect } from 'react';
import {
  Card,
  Accordion,
  Badge,
  Button,
  Table,
  Alert,
} from 'react-bootstrap';
import { medicalRecordAPI } from '../services/api';
import { FaUserMd, FaPills, FaAllergies, FaFileMedical } from 'react-icons/fa';

const MedicalRecord = ({ patientId }) => {
  const [record, setRecord] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (patientId) {
      loadMedicalRecord();
    }
  }, [patientId]);

  const loadMedicalRecord = async () => {
    try {
      setLoading(true);
      const response = await medicalRecordAPI.getByPatientId(patientId);
      setRecord(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load medical record');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-5">Loading medical record...</div>;
  }

  if (error) {
    return <Alert variant="danger">{error}</Alert>;
  }

  if (!record) {
    return <Alert variant="warning">No medical record found</Alert>;
  }

  const { patient, consultations, prescriptions, allergies } = record;

  return (
    <div className="medical-record">
      {/* En-tête du patient */}
      <Card className="mb-4">
        <Card.Body>
          <div className="d-flex justify-content-between align-items-start">
            <div>
              <h4>
                {patient.firstName} {patient.lastName}
              </h4>
              <p className="text-muted mb-1">
                Patient ID: {patient.patientId}
              </p>
              <p className="text-muted mb-1">
                Date of Birth: {new Date(patient.dateOfBirth).toLocaleDateString()}
              </p>
              <p className="text-muted">Gender: {patient.gender}</p>
            </div>
            <div>
              <Badge bg="info" className="me-2">
                Active
              </Badge>
              <Button variant="outline-primary" size="sm">
                Edit Profile
              </Button>
            </div>
          </div>
        </Card.Body>
      </Card>

      <Row>
        <Col md={8}>
          {/* Consultations */}
          <Card className="mb-4">
            <Card.Header>
              <FaUserMd className="me-2" />
              <strong>Consultations</strong>
              <Badge bg="secondary" className="ms-2">
                {consultations?.length || 0}
              </Badge>
            </Card.Header>
            <Card.Body>
              <Accordion>
                {consultations?.map((consultation, index) => (
                  <Accordion.Item eventKey={index.toString()} key={index}>
                    <Accordion.Header>
                      {new Date(consultation.date).toLocaleDateString()} -{' '}
                      {consultation.diagnosis}
                    </Accordion.Header>
                    <Accordion.Body>
                      <p>
                        <strong>Symptoms:</strong> {consultation.symptoms}
                      </p>
                      <p>
                        <strong>Diagnosis:</strong> {consultation.diagnosis}
                      </p>
                      {consultation.notes && (
                        <p>
                          <strong>Notes:</strong> {consultation.notes}
                        </p>
                      )}
                      <p className="text-muted small">
                        Doctor ID: {consultation.doctorId}
                      </p>
                    </Accordion.Body>
                  </Accordion.Item>
                ))}
              </Accordion>
              {(!consultations || consultations.length === 0) && (
                <p className="text-muted text-center py-3">
                  No consultations recorded
                </p>
              )}
            </Card.Body>
          </Card>

          {/* Prescriptions */}
          <Card className="mb-4">
            <Card.Header>
              <FaPills className="me-2" />
              <strong>Prescriptions</strong>
              <Badge bg="secondary" className="ms-2">
                {prescriptions?.length || 0}
              </Badge>
            </Card.Header>
            <Card.Body>
              <Table striped bordered hover size="sm">
                <thead>
                  <tr>
                    <th>Medication</th>
                    <th>Dosage</th>
                    <th>Frequency</th>
                    <th>Period</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {prescriptions?.map((prescription, index) => {
                    const endDate = new Date(prescription.endDate);
                    const isActive = !prescription.endDate || endDate > new Date();

                    return (
                      <tr key={index}>
                        <td>{prescription.medication}</td>
                        <td>{prescription.dosage}</td>
                        <td>{prescription.frequency}</td>
                        <td>
                          {new Date(prescription.startDate).toLocaleDateString()}
                          {prescription.endDate && (
                            <> - {endDate.toLocaleDateString()}</>
                          )}
                        </td>
                        <td>
                          <Badge bg={isActive ? 'success' : 'secondary'}>
                            {isActive ? 'Active' : 'Completed'}
                          </Badge>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </Table>
              {(!prescriptions || prescriptions.length === 0) && (
                <p className="text-muted text-center py-3">
                  No prescriptions recorded
                </p>
              )}
            </Card.Body>
          </Card>
        </Col>

        <Col md={4}>
          {/* Allergies */}
          <Card className="mb-4">
            <Card.Header>
              <FaAllergies className="me-2" />
              <strong>Allergies</strong>
            </Card.Header>
            <Card.Body>
              {allergies?.map((allergy, index) => (
                <Alert
                  key={index}
                  variant={
                    allergy.severity === 'SEVERE' ? 'danger' :
                    allergy.severity === 'MODERATE' ? 'warning' : 'info'
                  }
                  className="py-2"
                >
                  <strong>{allergy.substance}</strong>
                  <br />
                  <small>
                    Severity: {allergy.severity}
                    <br />
                    Reaction: {allergy.reaction}
                  </small>
                </Alert>
              ))}
              {(!allergies || allergies.length === 0) && (
                <p className="text-muted text-center py-3">
                  No allergies recorded
                </p>
              )}
            </Card.Body>
          </Card>

          {/* Actions rapides */}
          <Card>
            <Card.Header>
              <FaFileMedical className="me-2" />
              <strong>Quick Actions</strong>
            </Card.Header>
            <Card.Body>
              <div className="d-grid gap-2">
                <Button variant="primary">
                  Add New Consultation
                </Button>
                <Button variant="success">
                  Prescribe Medication
                </Button>
                <Button variant="warning">
                  Update Allergies
                </Button>
                <Button variant="outline-danger">
                  Emergency Contact
                </Button>
                <Button variant="outline-secondary">
                  Export Record
                </Button>
              </div>
            </Card.Body>
          </Card>

          {/* Informations de contact */}
          <Card className="mt-4">
            <Card.Body>
              <h6>Contact Information</h6>
              {patient.contact && (
                <>
                  <p className="mb-1">
                    <strong>Email:</strong> {patient.contact.email}
                  </p>
                  <p className="mb-1">
                    <strong>Phone:</strong> {patient.contact.phone}
                  </p>
                  <p className="mb-0">
                    <strong>Address:</strong>{' '}
                    {patient.contact.address.street}, {patient.contact.address.city}
                  </p>
                </>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default MedicalRecord;