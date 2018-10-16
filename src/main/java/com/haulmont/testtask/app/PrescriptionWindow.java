package com.haulmont.testtask.app;


import com.haulmont.testtask.dao.DAOFactory;
import com.haulmont.testtask.dao.PrescriptionDAO;
import com.haulmont.testtask.entity.Doctor;
import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.entity.Prescription;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

public class PrescriptionWindow extends Window {
    private Table table;
    private Object itemId;

    private TextField descriptionText;
    private ComboBox patientComboBox;
    private ComboBox doctorComboBox;
    private PopupDateField createDateField;
    private IntegerField expirationText;
    private ComboBox priorityComboBox;

    private Button okButton;
    private Button cancelButton;


    private static Logger logger = Logger.getLogger(PrescriptionWindow.class.getName());

    private static final String REQUIRED = "Обязательное для заполнения поле";
    private static final String FORMAT = "Укажите дату в формате \"дд.мм.гггг\"";
    private static final String RANGE = "Введите значение от 1 до 999";

    PrescriptionWindow(Table table, Object itemId) {
        this.table = table;
        this.itemId = itemId;
        createUI();
        fillPatients();
        fillDoctors();
        fillPriorities();
        processItem();
    }

    private void createUI() {
        setStyleName(AppStyle.MODAL_WINDOW);
        setWidth("480px");
        setHeight("400px");
        setModal(true);
        setResizable(false);
        center();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setMargin(false);
        formLayout.setSpacing(true);

        descriptionText = new TextField("Описание");
        descriptionText.setImmediate(true);
        descriptionText.setNullRepresentation("");
        descriptionText.setRequired(true);
        descriptionText.setRequiredError(REQUIRED);
        descriptionText.addValidator(new NullValidator(REQUIRED, false));
        descriptionText.setValidationVisible(false);
        descriptionText.setMaxLength(200);
        descriptionText.setWidth("100%");


        patientComboBox = new ComboBox("Пациент");
        patientComboBox.setImmediate(true);
        patientComboBox.setRequired(true);
        patientComboBox.setRequiredError(REQUIRED);
        patientComboBox.addValidator(new NullValidator(REQUIRED, false));
        patientComboBox.setValidationVisible(false);
        patientComboBox.setNullSelectionAllowed(false);
        patientComboBox.setTextInputAllowed(false);
        patientComboBox.setPageLength(5);
        patientComboBox.setWidth("100%");


        doctorComboBox = new ComboBox("Врач");
        doctorComboBox.setImmediate(true);
        doctorComboBox.setRequired(true);
        doctorComboBox.setRequiredError(REQUIRED);
        doctorComboBox.addValidator(new NullValidator(REQUIRED, false));
        doctorComboBox.setValidationVisible(false);
        doctorComboBox.setNullSelectionAllowed(false);
        doctorComboBox.setTextInputAllowed(false);
        doctorComboBox.setPageLength(5);
        doctorComboBox.setWidth("100%");

        createDateField = new PopupDateField("Дата создания");
        createDateField.setDateFormat("dd.MM.yyyy");
        createDateField.setInputPrompt("дд.мм.гггг");
        createDateField.setImmediate(true);
        Date minDate = new GregorianCalendar(1900, 0, 1).getTime();
        Date maxDate = new GregorianCalendar(2100, 11, 31).getTime();
        createDateField.setRangeStart(minDate);
        createDateField.setRangeEnd(maxDate);
        createDateField.addValidator(new DateRangeValidator(FORMAT, minDate, maxDate, Resolution.DAY));
        createDateField.setDateOutOfRangeMessage(FORMAT);
        createDateField.setConversionError(FORMAT);
        createDateField.setParseErrorMessage(FORMAT);
        createDateField.setWidth("100%");

        expirationText = new IntegerField("Срок действия в днях");
        expirationText.setConverter(new StringToIntegerConverter());
        expirationText.setImmediate(true);
        expirationText.setNullRepresentation("");
        expirationText.setRequired(true);
        expirationText.setRequiredError(RANGE);
        expirationText.addValidator(new NullValidator(REQUIRED, false));
        expirationText.setValidationVisible(false);
        expirationText.setMaxLength(3);
        expirationText.setWidth("100%");

        priorityComboBox = new ComboBox("Приоритет");
        priorityComboBox.setImmediate(true);
        priorityComboBox.setRequired(true);
        priorityComboBox.setRequiredError(REQUIRED);
        priorityComboBox.addValidator(new NullValidator(REQUIRED, false));
        priorityComboBox.setValidationVisible(false);
        priorityComboBox.setNullSelectionAllowed(false);
        priorityComboBox.setTextInputAllowed(false);
        priorityComboBox.setPageLength(3);
        priorityComboBox.setWidth("100%");

        formLayout.addComponents(descriptionText, patientComboBox, doctorComboBox, createDateField, expirationText, priorityComboBox);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);

        okButton = new Button("ОК");
        okButton.setWidth("125px");
        cancelButton = new Button("Отменить");
        cancelButton.setWidth("125px");

        buttonsLayout.addComponents(okButton, cancelButton);
        mainLayout.addComponents(formLayout, buttonsLayout);
        mainLayout.setExpandRatio(formLayout, 1f);
        mainLayout.setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);
        setContent(mainLayout);
    }

    private void fillPatients() {
        try {
            List<Patient> patients = DAOFactory.getInstance().getPatientDAO().getAll();
            IndexedContainer container = new IndexedContainer();
            container.addContainerProperty("patientText", String.class, null);
            container.addContainerProperty("lastName", String.class, null);
            container.addContainerProperty("firstName", String.class, null);
            container.addContainerProperty("middleName", String.class, null);
            container.addContainerProperty("phoneNumber", String.class, null);
            container.addContainerProperty("patient", Patient.class, null);
            for (Patient patient : patients) {
                String lastName = patient.getLastName();
                String firstName = patient.getFirstName();
                String middleName = patient.getMiddleName();
                String phoneNumber = patient.getPhoneNumber();
                Object patientId = patient.getId();
                String patientText = lastName + " " + firstName + " " + middleName + ", " + phoneNumber;
                Item item = container.addItem(patientId);
                if (item != null) {
                    item.getItemProperty("patientText").setValue(patientText);
                    item.getItemProperty("lastName").setValue(lastName);
                    item.getItemProperty("firstName").setValue(firstName);
                    item.getItemProperty("middleName").setValue(middleName);
                    item.getItemProperty("phoneNumber").setValue(phoneNumber);
                    item.getItemProperty("patient").setValue(patient);
                }
            }
            container.sort(new Object[]{"lastName", "firstName", "middleName"}, new boolean[]{true, true, true});
            patientComboBox.removeAllItems();
            patientComboBox.setContainerDataSource(container);
            patientComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            patientComboBox.setItemCaptionPropertyId("patientText");
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void fillDoctors() {
        try {
            List<Doctor> doctors = DAOFactory.getInstance().getDoctorDAO().getAll();
            IndexedContainer container = new IndexedContainer();
            container.addContainerProperty("doctorText", String.class, null);
            container.addContainerProperty("lastName", String.class, null);
            container.addContainerProperty("firstName", String.class, null);
            container.addContainerProperty("middleName", String.class, null);
            container.addContainerProperty("specialization", String.class, null);
            container.addContainerProperty("doctor", Doctor.class, null);
            for (Doctor doctor : doctors) {
                String lastName = doctor.getLastName();
                String firstName = doctor.getFirstName();
                String middleName = doctor.getMiddleName();
                String specialization = doctor.getSpecialization();
                Object doctorId = doctor.getId();
                String doctorText = lastName + " " + firstName + " " + middleName + ", " + specialization;
                Item item = container.addItem(doctorId);
                if (item != null) {
                    item.getItemProperty("doctorText").setValue(doctorText);
                    item.getItemProperty("lastName").setValue(lastName);
                    item.getItemProperty("firstName").setValue(firstName);
                    item.getItemProperty("middleName").setValue(middleName);
                    item.getItemProperty("specialization").setValue(specialization);
                    item.getItemProperty("doctor").setValue(doctor);
                }
            }
            container.sort(new Object[]{"lastName", "firstName", "middleName"}, new boolean[]{true, true, true});
            doctorComboBox.removeAllItems();
            doctorComboBox.setContainerDataSource(container);
            doctorComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            doctorComboBox.setItemCaptionPropertyId("doctorText");
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void fillPriorities() {
        try {
            String[] priorities = {"Нормальный","Срочный","Немедленный"};
            IndexedContainer container = new IndexedContainer();
            container.addContainerProperty("priority", String.class, null);
            for (int i = 0; i<priorities.length; i++) {
                String priority = priorities[i];
                Item item = container.addItem(i);
                if (item != null) {
                    item.getItemProperty("priority").setValue(priority);
                }
            }
            //container.sort(new Object[] {"priority"}, new boolean[] {true});
            priorityComboBox.removeAllItems();
            priorityComboBox.setContainerDataSource(container);
            priorityComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            priorityComboBox.setItemCaptionPropertyId("priority");
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void processItem() {
        if (itemId != null) {
            setCaption(" Редактирование рецепта");
            if (table != null) {
                Item item = table.getItem(itemId);
                if (item != null) {
                    try {
                        String description = String.valueOf(item.getItemProperty("description").getValue());
                        Patient patient = (Patient) item.getItemProperty("patient").getValue();
                        Doctor doctor = (Doctor) item.getItemProperty("doctor").getValue();
                        Date createDate = (Date) item.getItemProperty("createDate").getValue();
                        int expiration = (int) item.getItemProperty("expiration").getValue();
                        String priority = String.valueOf(item.getItemProperty("priority").getValue());
                        Object patientId = patient.getId();
                        Object doctorId = doctor.getId();
                        descriptionText.setValue(description);
                        patientComboBox.setValue(patientId);
                        doctorComboBox.setValue(doctorId);
                        createDateField.setValue(createDate);
                        expirationText.setValue(String.valueOf(expiration));
                        priorityComboBox.setValue(priority);
                    } catch (Exception e) {
                        logger.severe(e.getMessage());
                    }
                }
            }
        } else {
            setCaption(" Добавление рецепта");
            descriptionText.focus();
        }

        okButton.addClickListener(clickEvent -> {
            if (isValidFields()) {
                try {
                    Prescription prescription = new Prescription();

                    prescription.setDescription(descriptionText.getValue());

                    Object patientItemId = patientComboBox.getValue();
                    Item patientItem = patientComboBox.getItem(patientItemId);
                    Patient patient = (Patient) patientItem.getItemProperty("patient").getValue();
                    prescription.setPatient(patient);

                    Object doctorItemId = doctorComboBox.getValue();
                    Item doctorItem = doctorComboBox.getItem(doctorItemId);
                    Doctor doctor = (Doctor) doctorItem.getItemProperty("doctor").getValue();
                    prescription.setDoctor(doctor);

                    prescription.setCreateDate(createDateField.getValue());

                    prescription.setExpiration(Integer.parseInt(expirationText.getValue()));

                    Object priorityItemId = priorityComboBox.getValue();
                    Item priorityItem = priorityComboBox.getItem(priorityItemId);
                    String priority = (String) priorityItem.getItemProperty("priority").getValue();
                    prescription.setPriority(priority);

                    PrescriptionDAO prescriptionDAO = DAOFactory.getInstance().getPrescriptionDAO();
                    if (itemId != null) {
                        Long id = (Long) itemId;
                        prescription.setId(id);
                        prescriptionDAO.update(prescription);
                        if (table != null) {
                            Item item = table.getItem(itemId);
                            if (item != null) {
                                item.getItemProperty("description").setValue(prescription.getDescription());
                                item.getItemProperty("patient").setValue(prescription.getPatient());
                                item.getItemProperty("doctor").setValue(prescription.getDoctor());
                                item.getItemProperty("createDate").setValue(prescription.getCreateDate());
                                item.getItemProperty("expiration").setValue(prescription.getExpiration());
                                item.getItemProperty("priority").setValue(prescription.getPriority());
                            }
                        }
                    } else {
                        prescription = prescriptionDAO.persist(prescription);
                        if (prescription != null && prescription.getId() != null) {
                            if (table != null) {
                                Object itemId = table.addItem(new Object[] {
                                                prescription.getDescription(),
                                                prescription.getPatient(),
                                                prescription.getDoctor(),
                                                prescription.getCreateDate(),
                                                prescription.getExpiration(),
                                                prescription.getPriority()},
                                                prescription.getId());
                                if (itemId != null) {
                                    table.select(itemId);
                                    table.setCurrentPageFirstItemId(itemId);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                }
                close();
            }
        });

        cancelButton.addClickListener(clickEvent -> close());
    }

    private boolean isValidFields() {
       boolean res = true;
         try {
            String strVal = descriptionText.getValue().trim();
            ObjectProperty<String> strProp = new ObjectProperty<>(strVal);
            descriptionText.setPropertyDataSource(strProp);
            descriptionText.validate();
        } catch (Exception e) {
            res = false;
        }
        try {
            patientComboBox.validate();
        } catch (Exception e) {
            res = false;
        }
        try {
            doctorComboBox.validate();
        } catch (Exception e) {
            res = false;
        }
        try {
            createDateField.validate();
        } catch (Exception e) {
            res = false;
        }
        try {
            String strVal = expirationText.getValue().trim();
            ObjectProperty<String> strProp = new ObjectProperty<>(strVal);
            expirationText.setPropertyDataSource(strProp);
            expirationText.validate();
        } catch (Exception e) {
            res = false;
        }
        try {
            priorityComboBox.validate();
        } catch (Exception e) {
            res = false;
        }
        descriptionText.setValidationVisible(!descriptionText.isValid());
        patientComboBox.setValidationVisible(!patientComboBox.isValid());
        doctorComboBox.setValidationVisible(!doctorComboBox.isValid());
        createDateField.setValidationVisible(!createDateField.isValid());
        expirationText.setValidationVisible(!expirationText.isValid());
        priorityComboBox.setValidationVisible(!priorityComboBox.isValid());
        return res;
    }
}