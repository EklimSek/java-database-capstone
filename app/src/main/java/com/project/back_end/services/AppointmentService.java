package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private com.project.back_end.services.Service service;

    public int bookAppointment(Appointment appointment) {
    try {
        if (appointment.getPatient() == null || 
            !patientRepository.existsById(appointment.getPatient().getId())) {
            return 0;
        }
        appointmentRepository.save(appointment);
        return 1;
    } catch (Exception e) {
        return 0;
    }
}

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        int validationResult = service.validateAppointment(appointment);
        if (validationResult == -1) {
            response.put("message", "Invalid doctor ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (validationResult == 0) {
            response.put("message", "Requested appointment time is unavailable");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        try {
            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Error updating appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Appointment appointment = appointmentOpt.get();

        String patientEmail = tokenService.extractIdentifier(token);
        if (patientEmail == null || !appointment.getPatient().getEmail().equals(patientEmail)) {
            response.put("message", "You are not authorized to cancel this appointment");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Error cancelling appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        String doctorEmail = tokenService.extractIdentifier(token);
        Long doctorId = doctorRepository.findByEmail(doctorEmail).getId();

        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        List<Appointment> appointments;

        if (pname != null && !pname.isEmpty()) {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, pname, startOfDay, endOfDay);
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);
        }

        result.put("appointments", appointments);
        return result;
    }
}