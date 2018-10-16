package com.haulmont.testtask.app;


import com.haulmont.testtask.entity.Patient;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

public class PatientConverter implements Converter<String, Patient> {
    
    @Override
    public Patient convertToModel(String s, Class<? extends Patient> aClass, Locale locale) throws ConversionException {
        if (s == null) {
            return null;
        }
        String[] parts = s.split("\n");
        if (parts.length != 3) {
            throw new ConversionException("Can't convert String to Patient: " + s);
        }
        Patient patient = new Patient();
        try {
            patient.setId(Long.valueOf(parts[0]));
            patient.setLastName(String.valueOf(parts[1]));
            patient.setLastName(String.valueOf(parts[2]));
            patient.setLastName(String.valueOf(parts[3]));
            patient.setLastName(String.valueOf(parts[4]));
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
        return patient;
    }

    @Override
    public String convertToPresentation(Patient patient, Class<? extends String> aClass, Locale locale) throws ConversionException {
        if (patient != null) {
            return patient.getLastName() + " " + patient.getFirstName() + " " + patient.getMiddleName() + ", " + patient.getPhoneNumber();
        } else {
            return null;
        }
    }

    @Override
    public Class<Patient> getModelType() {
        return Patient.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
