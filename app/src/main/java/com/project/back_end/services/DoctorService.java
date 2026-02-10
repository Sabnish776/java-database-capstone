package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }
// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date){

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if(doctor == null){
            return Collections.emptyList();
        }
        List<String> availableTimes = doctor.getAvailableTimes();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23,59,59) ;
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctor_IdAndAppointmentTimeBetween(doctorId,start,end);

        for(Appointment appointment : bookedAppointments){
            String bookedSlot = appointment.getAppointmentTime().toLocalTime().toString() ;
                availableTimes.removeIf(time -> time.startsWith(bookedSlot));
        }
        return availableTimes;
    }
// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.
    public int saveDoctor(Doctor doctor){
        try {
            Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail()) ;
            if (existingDoctor != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0 ;
        }
    }
// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.
    public int updateDoctor(Doctor doctor){
        try {
            Doctor existingDoctor = doctorRepository.findById(doctor.getId()).orElse(null);
            if (existingDoctor == null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        }catch (Exception e){
            return 0 ;
        }
    }
// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors(){
        return doctorRepository.findAll();
    }
// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.
    @Transactional()
    public int deleteDoctor(Long id){
        try{
            Doctor doctor = doctorRepository.findById(id).orElse(null);
            if(doctor == null) return -1 ;
            doctorRepository.delete(doctor);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }
// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.
    public ResponseEntity<Map<String,String>> validateDoctor(Login login){
        Map<String,String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if(doctor==null){
            response.put("message","Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if(!doctor.getPassword().equals(login.getPassword())){
            response.put("message","Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        String token = tokenService.generateToken(login.getEmail());
        response.put("token",token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.
    @Transactional(readOnly = true)
    public Map<String,Object> findDoctorByName(String name){
        Map<String,Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name) ;
        response.put("doctors",doctors);
        return response;
    }

// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.
    public Map<String , Object> filterDoctorsByNameSpecialtyAndTime(String name , String Specialty,String amOrPm){
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name,Specialty);
        doctors = filterDoctorByTime(doctors , amOrPm) ;
        Map<String , Object> response = new HashMap<>();
        response.put("doctors",doctors);
        return response;
    }

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors , String amOrPm) {
        if(amOrPm == null){
            return doctors;
        }
        return doctors.stream()
                .filter(
                        doctor -> doctor.getAvailableTimes() != null &&
                                doctor.getAvailableTimes().stream().anyMatch( slot -> {
                                        int hour = Integer.parseInt(slot.substring(0,2));
                                        if(amOrPm.equalsIgnoreCase("AM")){
                                            return hour < 12 ;
                                        }else if(amOrPm.equalsIgnoreCase("PM")){
                                            return hour >= 12 ;
                                        }
                                        return false ;
                                })
                ).toList() ;
    }

// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).
    public Map<String, Object> filterDoctorByNameAndTime(String name , String amOrPm){
        Map<String,Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name) ;
        doctors = filterDoctorByTime(doctors,amOrPm) ;
        response.put("doctors",doctors);
        return response;
    }
// 14. **filterDoctorByNameAndSpeciality Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.
    public Map<String,Object> filterDoctorByNameAndSpecialty(String name , String Specialty){
        Map<String,Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name,Specialty);
        response.put("doctors",doctors);
        return response;
    }

// 15. **filterDoctorByTimeAndSpeciality Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).
    public Map<String,Object> filterDoctorByTimeAndSpecialty(String amOrPm , String Specialty){
        Map<String,Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(Specialty);
        doctors = filterDoctorByTime(doctors,amOrPm) ;
        response.put("doctors",doctors);
        return response;
    }
// 16. **filterDoctorBySpeciality Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.
    public Map<String,Object> filterDoctorBySpecialty(String Specialty){
        Map<String,Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(Specialty);
        response.put("doctors",doctors);
        return response;
    }
// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.
    public Map<String , Object> filterDoctorsByTime(String amOrPm){
        Map<String , Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll() ;
        doctors = filterDoctorByTime(doctors, amOrPm) ;
        response.put("doctors",doctors) ;
        return response ;
    }
   
}
