package com.haulmont.testtask.app;


import com.haulmont.testtask.entity.Patient;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class PatientFilter implements Container.Filter {

    private String propertyId;
    private String filterString;
    private  boolean ignoreCase;

    PatientFilter(String propertyId, String filterString, boolean ignoreCase) {
        this.propertyId = propertyId;
        this.ignoreCase = ignoreCase;
        this.filterString = filterString;
    }

    /** Apply the filter on a Patient item to check if it passes. */
    @Override
    public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
        Property<?> patientProp = item.getItemProperty(propertyId);
        if (patientProp == null || !patientProp.getType().equals(Patient.class)) {
            return false;
        }
        if (filterString.isEmpty()) {
            return true;
        }
        try {
            Patient patient = (Patient) patientProp.getValue();
            String filterStr = patient.getLastName()+" "+patient.getFirstName()+" "+patient.getMiddleName();
            if (ignoreCase) {
                return filterStr.toLowerCase().contains(filterString.toLowerCase());
            } else {
                return filterStr.contains(filterString);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /** Tells if this filter works on the given property. */
    @Override
    public boolean appliesToProperty(Object propertyId) {
        return propertyId != null && propertyId.equals(this.propertyId);
    }
}