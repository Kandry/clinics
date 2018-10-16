package com.haulmont.testtask.app;


import com.haulmont.testtask.entity.Doctor;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

public class DoctorConverter implements Converter<String, Doctor> {

    @Override
    public Doctor convertToModel(String s, Class<? extends Doctor> aClass, Locale locale) throws ConversionException {
        if (s == null) {
            return null;
        }
        String[] parts = s.split("\n");
        if (parts.length != 3) {
            throw new ConversionException("Can't convert String to Doctor: " + s);
        }
        Doctor doctor = new Doctor();
        try {
            doctor.setId(Long.valueOf(parts[0]));
            doctor.setLastName(String.valueOf(parts[1]));
            doctor.setLastName(String.valueOf(parts[2]));
            doctor.setLastName(String.valueOf(parts[3]));
            doctor.setLastName(String.valueOf(parts[4]));
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
        return doctor;
    }

    @Override
    public String convertToPresentation(Doctor doctor, Class<? extends String> aClass, Locale locale) throws ConversionException {
        if (doctor != null) {
            return doctor.getLastName() + " " + doctor.getFirstName() + " " + doctor.getMiddleName() + ", " + doctor.getSpecialization();
        } else {
            return null;
        }
    }

    @Override
    public Class<Doctor> getModelType() {
        return Doctor.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
