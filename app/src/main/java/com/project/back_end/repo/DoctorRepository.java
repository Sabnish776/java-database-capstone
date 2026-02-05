package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
// 2. Custom Query Methods:
    Doctor findByEmail(String email);

//    - **findByNameLike**:
//      - This method retrieves a list of Doctors whose name contains the provided search string (case-sensitive).
//      - The `CONCAT('%', :name, '%')` is used to create a pattern for partial matching.
//      - Return type: List<Doctor>
//      - Parameters: String name
    @Query("""
            select distinct d from Doctor d where d.name like CONCAT('%', :name, '%')
            """)
    List<Doctor> findByNameLike(@Param("name") String name);

//    - **findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase**:
//      - This method retrieves a list of Doctors where the name contains the search string (case-insensitive) and the specialty matches exactly (case-insensitive).
//      - It combines both fields for a more specific search.
//      - Return type: List<Doctor>
//      - Parameters: String name, String specialty
    @Query("""
            select distinct d from Doctor d where lower(d.name) like lower(CONCAT('%', :name, '%'))
                        and lower(d.specialty)= lower(:specialty)
            """)
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            @Param("name") String name, @Param("specialty") String specialty);
//    - **findBySpecialtyIgnoreCase**:
//      - This method retrieves a list of Doctors with the specified specialty, ignoring case sensitivity.
//      - Return type: List<Doctor>
//      - Parameters: String specialty
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

}