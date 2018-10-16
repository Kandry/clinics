package com.haulmont.testtask.entity;


public interface EntityPeople extends Entity {
    public String getFirstName();
    public void setFirstName(String firstName);
    public String getLastName();
    public void setLastName(String lastName);
    public String getMiddleName();
    public void setMiddleName(String middleName);
}
