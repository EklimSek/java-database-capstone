package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Doctor doctor = doctorOpt.get();
        List<String> allSlots = doctor.getAvailableTimes();

        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

        List<String> bookedSlots = new ArrayList<>();
        for (Appointment appointment : bookedAppointments) {
            String slotStart = appointment.getAppointmentTimeOnly().format(TIME_FORMAT);
            bookedSlots.add(slotStart);
        }

        List<String> availableSlots = new ArrayList<>();
        for (String slot : allSlots) {
            String slotStartTime = slot.split("-")[0].trim();
            if (!bookedSlots.contains(slotStartTime)) {
                availableSlots.add(slot);
            }
        }

        return availableSlots;
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingOpt = doctorRepository.findById(doctor.getId());
            if (existingOpt.isEmpty()) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> existingOpt = doctorRepository.findById(id);
            if (existingOpt.isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(com.project.back_end.DTO.Login login) {
        Map<String, String> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if (doctor == null) {
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if (!doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(doctor.getEmail());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", filtered);
        return result;
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", filtered);
        return result;
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", filtered);
        return result;
    }

    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll();
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", filtered);
        return result;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        List<Doctor> filtered = new ArrayList<>();

        for (Doctor doctor : doctors) {
            boolean matches = false;
            for (String slot : doctor.getAvailableTimes()) {
                String startTime = slot.split("-")[0].trim();
                int hour = Integer.parseInt(startTime.split(":")[0]);

                if ("AM".equalsIgnoreCase(amOrPm) && hour < 12) {
                    matches = true;
                    break;
                } else if ("PM".equalsIgnoreCase(amOrPm) && hour >= 12) {
                    matches = true;
                    break;
                }
            }
            if (matches) {
                filtered.add(doctor);
            }
        }

        return filtered;
    }
}