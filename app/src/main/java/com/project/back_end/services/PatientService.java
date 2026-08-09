package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !patient.getId().equals(id)) {
            response.put("message", "Unauthorized access to patient records");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        List<AppointmentDTO> appointmentDTOs = convertToDTOList(appointments);

        response.put("appointments", appointmentDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);
        List<AppointmentDTO> appointmentDTOs = convertToDTOList(appointments);

        response.put("appointments", appointmentDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
        List<AppointmentDTO> appointmentDTOs = convertToDTOList(appointments);

        response.put("appointments", appointmentDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();

        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);
        List<AppointmentDTO> appointmentDTOs = convertToDTOList(appointments);

        response.put("appointments", appointmentDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("patient", patient);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private List<AppointmentDTO> convertToDTOList(List<Appointment> appointments) {
        List<AppointmentDTO> dtos = new ArrayList<>();

        for (Appointment appointment : appointments) {
            Doctor doctor = appointment.getDoctor();
            Patient patient = appointment.getPatient();

            AppointmentDTO dto = new AppointmentDTO(
                    appointment.getId(),
                    doctor.getId(),
                    doctor.getName(),
                    patient.getId(),
                    patient.getName(),
                    patient.getEmail(),
                    patient.getPhone(),
                    patient.getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()
            );

            dtos.add(dto);
        }

        return dtos;
    }
}