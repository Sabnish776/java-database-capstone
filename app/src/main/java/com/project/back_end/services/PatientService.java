package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }
// 3. **createPatient Method**:
//    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
//    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
//    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.
    public int createPatient(Patient patient){
        try{
            patientRepository.save(patient);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }
// 4. **getPatientAppointment Method**:
//    - Retrieves a list of appointments for a specific patient, based on their ID.
//    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
//    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
//    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String,Object>> getPatientAppointment(Long id, String token){
        Map<String,Object> response = new HashMap<>();
        String patientEmail = tokenService.extractEmail(token) ;
        Patient patient = patientRepository.findByEmail(patientEmail);
        if(patient == null || !id.equals(patient.getId())){
            response.put("message","Unauthorized access");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        List<Appointment> appointments = appointmentRepository.findByPatient_Id(id) ;
        List<AppointmentDTO> dtoList = appointments.stream()
                .map(AppointmentDTO::new)
                .toList();
        response.put("appointments",dtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// 5. **filterByCondition Method**:
//    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
//    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
//    - Converts the appointments into `AppointmentDTO` and returns them in the response.
//    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.
    public ResponseEntity<Map<String,Object>> filterByCondition(String condition , Long id){
        Map<String,Object> response = new HashMap<>();
        int status = 0;
        if(condition.equalsIgnoreCase("past") ){
            status = 1;
        }else if(condition.equalsIgnoreCase("future") ){
            status = 0;
        } else{
            response.put("message","Invalid condition");
        }
        List<Appointment> appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status) ;
        List<AppointmentDTO> dtoList = appointments.stream()
                .map(AppointmentDTO::new)
                .toList();
        response.put("appointments",dtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// 6. **filterByDoctor Method**:
//    - Filters appointments for a patient based on the doctor's name.
//    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
//    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.
    public ResponseEntity<Map<String,Object>> filterByDoctor(String name, Long patientId){
        Map<String,Object> response = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByDoctor_NameContainingIgnoreCaseAndPatient_Id(name,patientId) ;
        List<AppointmentDTO> dtoList = appointments.stream()
                .map(AppointmentDTO::new)
                .toList();
        response.put("appointments",dtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// 7. **filterByDoctorAndCondition Method**:
//    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
//    - This method combines filtering by doctor name and appointment status (past or future).
//    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
//    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.
    public ResponseEntity<Map<String,Object>> filterByDoctorAndCondition(String condition , String name , Long patientId){
        Map<String,Object> response = new HashMap<>();
        int status;
        if(condition.equalsIgnoreCase("past") ){
            status = 1;
        }else if(condition.equalsIgnoreCase("future") ){
            status = 0;
        } else{
            status = 0;
            response.put("message","Invalid condition");
        }
        List<Appointment> appointments = appointmentRepository.findByDoctor_NameContainingIgnoreCaseAndPatient_Id(name,patientId) ;
        appointments = appointments.stream()
                .filter(appointment -> appointment.getStatus() == status )
                .toList() ;
        List<AppointmentDTO> dtoList = appointments.stream()
                .map(AppointmentDTO::new)
                .toList();
        response.put("appointments",dtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// 8. **getPatientDetails Method**:
//    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
//    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
//    - It returns the patient's information in the response body.
    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.
    public ResponseEntity<Map<String,Object>> getPatientDetails(String token){
        Map<String,Object> response = new HashMap<>();
        String patientEmail = tokenService.extractEmail(token) ;
        Patient patient = patientRepository.findByEmail(patientEmail);
        if(patient == null){
            response.put("message","Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("patient",patient);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
