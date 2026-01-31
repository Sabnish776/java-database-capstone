package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id ;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Doctor doctor ;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Patient patient ;

    @FutureOrPresent(message = "Appointment time must be in future")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Status must not be null")
    private int status ;

    @Transient
    public LocalDateTime getEndTime(){
        return appointmentTime.plusHours(1);
    }
    @Transient
    public LocalDate getAppointmentDate(){
        return appointmentTime.toLocalDate();
    }
    @Transient
    public LocalTime getAppointmentTimeOnly(){
        return appointmentTime.toLocalTime();
    }

    public Appointment(){}

    public Appointment(Long id, Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "doctor=" + doctor +
                ", patient=" + patient +
                ", appointmentTime=" + appointmentTime +
                ", status=" + status +
                '}';
    }
}

