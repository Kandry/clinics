package com.haulmont.testtask.entity;


import java.util.Date;

public class Prescription implements  Entity{
    private Long id = null;
    private String description;
    private Patient patient;
    private Doctor doctor;
    private Date createDate;
    private Integer expiration;
    private String priority;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription(){return this.description;}

    public void setDescription(String description){this.description = description;}

    public Patient getPatient(){
        return this.patient;
    }

    public void setPatient(Patient patient){
        this.patient = patient;
    }

    public Doctor getDoctor(){
        return this.doctor;
    }

    public void setDoctor(Doctor doctor){
        this.doctor = doctor;
    }

    public Date getCreateDate(){
        return this.createDate;
    }

    public void setCreateDate(Date createDate){
        this.createDate = createDate;
    }

    public Integer getExpiration(){
        return this.expiration;
    }

    public void setExpiration(Integer expiration){
        this.expiration = expiration;
    }

    public String getPriority(){
        return this.priority;
    }

    public void setPriority(String priority){
        this.priority = priority;
    }
}
