package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
@CrossOrigin(origins = "*")
public class DoctorController {
// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private Service service ;

// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String,Object>> getDoctorAvailability(
            @PathVariable LocalDate date, @PathVariable Long doctorId, @PathVariable String user, @PathVariable String token){
        ResponseEntity<Map<String, String>> validated = service.validateToken(token, user);
        if(validated.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity<>(Map.of("message","Unauthorized access") , HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(Map.of("availability",doctorService.getDoctorAvailability(doctorId,date) ));
    }

// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        return ResponseEntity.ok(
                Map.of("doctors", doctorService.getDoctors())
        );
    }
// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.
    @PostMapping("/{token}")
    public ResponseEntity<Map<String,String>> saveDoctor(Doctor doctor, @PathVariable String token){
        ResponseEntity<Map<String, String>> validated = service.validateToken(token, "admin");
        if(validated.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity<>(Map.of("message","Unauthorized access") , HttpStatus.UNAUTHORIZED);
        }
        int status = doctorService.saveDoctor(doctor);
        if(status == -1){
            return new ResponseEntity<>(Map.of("conflict","Doctor already exists") , HttpStatus.BAD_REQUEST);
        }
        if(status == 0){
            return new ResponseEntity<>(Map.of("success","Internal error occurred") , HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Map.of("success","Successfully Added doctor") , HttpStatus.OK);
    }


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> doctorLogin(@RequestBody Login login){
        return doctorService.validateDoctor(login) ;
    }

// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.
    @PutMapping("/{token}")
    public ResponseEntity<Map<String ,String>> updateDoctor(@RequestBody Doctor doctor , @PathVariable String token){
        ResponseEntity<Map<String ,String>> validated = service.validateToken(token, "admin");
        if(validated.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity<>(Map.of("message","Unauthorized access") , HttpStatus.UNAUTHORIZED);
        }
        int status = doctorService.updateDoctor(doctor);
        if(status == -1){
            return new ResponseEntity<>(Map.of("message","Doctor doesnt exist") , HttpStatus.BAD_REQUEST);
        }
        if(status == 0){
            return new ResponseEntity<>(Map.of("message","Internal error occurred") , HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Map.of("success","Successfully updated doctor") , HttpStatus.OK);
    }

// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String , String>> deleteDoctor(
            @RequestBody Doctor doctor, @PathVariable Long id ,@PathVariable String token){
        ResponseEntity<Map<String ,String>> validated = service.validateToken(token, "admin");
        if(validated.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity<>(Map.of("message","Unauthorized access") , HttpStatus.UNAUTHORIZED);
        }
        int status = doctorService.deleteDoctor(id);
        if(status == -1){
            return new ResponseEntity<>(Map.of("message","Doctor doesnt exist") , HttpStatus.BAD_REQUEST);
        }
        if(status == 0){
            return new ResponseEntity<>(Map.of("message","Internal error occurred") , HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Map.of("message","Successfully deleted doctor") , HttpStatus.OK);
    }
// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String , Object>> filter(@PathVariable String name, @PathVariable String time, @PathVariable String speciality){
        if ("all".equals(name)) name = null;
        if ("all".equals(time)) time = null;
        if ("all".equals(speciality)) speciality = null;

        return ResponseEntity.ok(
                service.filterDoctor(name, speciality, time)
        );
    }

}
