import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Nav, Tab } from 'react-bootstrap';
import { Line, Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import PatientList from './PatientList';
import MedicalRecord from './MedicalRecord';
import ConsultationForm from './ConsultationForm';
import { medicalRecordAPI } from '../services/api';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const Dashboard = () => {
  const [stats, setStats] = useState({});
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [activeTab, setActiveTab] = useState('patients');

  useEffect(() => {
    loadStatistics();
  }, []);

  const loadStatistics = async () => {
    try {
      const response = await medicalRecordAPI.getStats();
      setStats(response.data);
    } catch (error) {
      console.error('Failed to load statistics:', error);
    }
  };

  const handlePatientSelect = (patient) => {
    setSelectedPatient(patient);
    setActiveTab('record');
  };

  const chartData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'Consultations',
        data: [12, 19, 15, 25, 22, 30],
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
      },
      {
        label: 'Prescriptions',
        data: [8, 15, 12, 18, 16, 22],
        borderColor: 'rgb(255, 99, 132)',
        backgroundColor: 'rgba(255, 99, 132, 0.2)',
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Medical Activity Overview',
      },
    },
  };

  return (
    <Container fluid className="dashboard">
      <Row className="mb-4">
        <Col>
          <h2>Medical DME Dashboard</h2>
          <p className="text-muted">
            Système de Gestion de Dossiers Médicaux Électroniques
          </p>
        </Col>
      </Row>

      <Row className="mb-4">
        <Col md={3}>
          <Card className="stat-card">
            <Card.Body>
              <Card.Title>Total Patients</Card.Title>
              <Card.Text className="display-6">
                {stats.totalPatients || 0}
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="stat-card">
            <Card.Body>
              <Card.Title>Medical Records</Card.Title>
              <Card.Text className="display-6">
                {stats.totalRecords || 0}
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="stat-card">
            <Card.Body>
              <Card.Title>Active Cases</Card.Title>
              <Card.Text className="display-6">24</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="stat-card">
            <Card.Body>
              <Card.Title>Today's Appointments</Card.Title>
              <Card.Text className="display-6">8</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="mb-4">
        <Col md={8}>
          <Card>
            <Card.Body>
              <Line data={chartData} options={chartOptions} />
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card>
            <Card.Body>
              <Card.Title>System Status</Card.Title>
              <div className="system-status">
                <div className="status-item">
                  <span className="status-dot online"></span>
                  <span>REST API: Online</span>
                </div>
                <div className="status-item">
                  <span className="status-dot online"></span>
                  <span>SOAP Service: Online</span>
                </div>
                <div className="status-item">
                  <span className="status-dot online"></span>
                  <span>RMI Server: Online</span>
                </div>
                <div className="status-item">
                  <span className="status-dot online"></span>
                  <span>Database: Connected</span>
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col>
          <Tab.Container activeKey={activeTab} onSelect={setActiveTab}>
            <Card>
              <Card.Header>
                <Nav variant="tabs">
                  <Nav.Item>
                    <Nav.Link eventKey="patients">Patients</Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link eventKey="record" disabled={!selectedPatient}>
                      Medical Record
                    </Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link eventKey="consultation">
                      New Consultation
                    </Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link eventKey="reports">Reports</Nav.Link>
                  </Nav.Item>
                </Nav>
              </Card.Header>
              <Card.Body>
                <Tab.Content>
                  <Tab.Pane eventKey="patients">
                    <PatientList onPatientSelect={handlePatientSelect} />
                  </Tab.Pane>
                  <Tab.Pane eventKey="record">
                    {selectedPatient && (
                      <MedicalRecord patientId={selectedPatient.patientId} />
                    )}
                  </Tab.Pane>
                  <Tab.Pane eventKey="consultation">
                    <ConsultationForm />
                  </Tab.Pane>
                  <Tab.Pane eventKey="reports">
                    <ReportsTab />
                  </Tab.Pane>
                </Tab.Content>
              </Card.Body>
            </Card>
          </Tab.Container>
        </Col>
      </Row>
    </Container>
  );
};

const ReportsTab = () => (
  <div>
    <h4>Medical Reports</h4>
    <Row>
      <Col md={6}>
        <Card>
          <Card.Body>
            <Card.Title>Patient Statistics</Card.Title>
            {/* Graphiques de statistiques */}
          </Card.Body>
        </Card>
      </Col>
      <Col md={6}>
        <Card>
          <Card.Body>
            <Card.Title>Export Options</Card.Title>
            <Button variant="primary" className="me-2">
              Export to XML
            </Button>
            <Button variant="secondary" className="me-2">
              Export to PDF
            </Button>
            <Button variant="success">
              Sync with External Systems
            </Button>
          </Card.Body>
        </Card>
      </Col>
    </Row>
  </div>
);

export default Dashboard;