import axios from 'axios';
import { getToken, removeToken } from './auth';

const API_BASE_URL = '/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré ou invalide
      removeToken();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const medicalRecordAPI = {
  // CRUD Operations
  getAll: (page = 0, size = 10) =>
    api.get(`/medical-records?page=${page}&size=${size}`),

  getByPatientId: (patientId) =>
    api.get(`/medical-records/patient/${patientId}`),

  create: (recordData) =>
    api.post('/medical-records', recordData),

  update: (recordId, recordData) =>
    api.put(`/medical-records/${recordId}`, recordData),

  delete: (recordId) =>
    api.delete(`/medical-records/${recordId}`),

  // Consultations
  addConsultation: (recordId, consultationData) =>
    api.post(`/medical-records/${recordId}/consultations`, consultationData),

  getConsultations: (recordId) =>
    api.get(`/medical-records/${recordId}/consultations`),

  // Prescriptions
  addPrescription: (recordId, prescriptionData) =>
    api.post(`/medical-records/${recordId}/prescriptions`, prescriptionData),

  getActivePrescriptions: (recordId) =>
    api.get(`/medical-records/${recordId}/prescriptions/active`),

  // Search
  searchByDate: (startDate, endDate) =>
    api.get(`/medical-records/search/consultations`, {
      params: { startDate, endDate }
    }),

  // Statistics
  getStats: () => api.get('/medical-records/stats'),
};

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  refreshToken: () => api.post('/auth/refresh'),
};

export default api;