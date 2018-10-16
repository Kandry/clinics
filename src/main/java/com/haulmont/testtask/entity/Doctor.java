package com.haulmont.testtask.entity;


public class Doctor implements EntityPeople{
    private Long id = null;
    private String firstName;
    private String lastName;
    private String middleName;
    private String specialization;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getMiddleName() {
        return this.middleName;
    }

    @Override
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSpecialization(){
        return this.specialization;
    }

    public void setSpecialization(String specialization){
        this.specialization = specialization;
    }
}
