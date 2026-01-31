package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prescriptions")
public class Prescription {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id ;

    @NotNull
    @Size(min = 3 , max = 100)
    private String patientName ;

    @NotNull
    private Long appointmentId ;

    @NotNull
    @Size(min = 3 , max = 100)
    private String medication ;

    @NotNull
    private String dosage ;

    @Size(max = 200 )
    private String doctorNotes ;

    public Prescription() {
    }

    public Prescription(String id, Long appointmentId, String medication, String dosage, String doctorNotes) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
