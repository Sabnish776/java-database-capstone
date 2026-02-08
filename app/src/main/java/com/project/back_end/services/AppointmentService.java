package com.project.back_end.services;


import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.
    public AppointmentService(AppointmentRepository appointmentRepository , Service service , TokenService tokenService , PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }
// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try{
            appointmentRepository.save(appointment);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }
// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.
    @Transactional
    public ResponseEntity<Map<String,String>> updateAppointment(Appointment appointment){
        Map<String,String> response = new HashMap<>();
        Appointment existing = appointmentRepository.findById(appointment.getId()).orElse(null) ;
        if(existing == null){
            response.put("message","Appointment Not Found");
            return new  ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if(service.validateAppointment(appointment) != 1){
            response.put("message","Invalid Appointment");
            return new  ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        appointmentRepository.save(existing);
        response.put("message","Appointment Updated");
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }
// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.
    @Transactional
    public ResponseEntity<Map<String ,String>> cancelAppointment(Long id,String token){
        Map<String ,String> response = new HashMap<>();
        String patientEmail = tokenService.extractEmail(token) ;
        Long patientId = patientRepository.findByEmail(patientEmail).getId() ;
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if(appointment == null){
            response.put("message","Appointment Not Found");
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
        if(!appointment.getPatient().getId().equals(patientId)){
            response.put("message","Unauthorized to perform this operation");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }
        appointmentRepository.delete(appointment);
        response.put("message","Appointment Cancelled");
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }
// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.
    @Transactional(readOnly = true)
    public Map<String,Object> getAppointments(String pname , LocalDate date, String token){
        Map<String,Object> response = new HashMap<>();
        String doctorEmail = tokenService.extractEmail(token) ;
        Long doctorId = doctorRepository.findByEmail(doctorEmail).getId();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23,59,59) ;
        List<Appointment> appointments = appointmentRepository.findByDoctor_IdAndAppointmentTimeBetween(doctorId,start,end);
        if(pname != null || !pname.equalsIgnoreCase("null")){
            appointments = appointments.stream()
                    .filter(appointment -> appointment.getPatient().getName().toLowerCase().contains(pname.toLowerCase()))
                    .toList();
        }
        response.put("appointments",appointments);
        return response;
    }
// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.
    @Transactional
    public void changeStatus(long appointmentId, int status) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
}
