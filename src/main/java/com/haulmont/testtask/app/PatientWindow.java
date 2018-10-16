package com.haulmont.testtask.app;


import com.haulmont.testtask.dao.DAOFactory;
import com.haulmont.testtask.dao.PatientDAO;
import com.haulmont.testtask.entity.Patient;
import com.vaadin.data.Item;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.ui.*;

import java.util.logging.Logger;

public class PatientWindow extends Window {
    private Table table;
    private Object itemId;
    private Button okButton;
    private Button cancelButton;
    private TextField lastNameText;
    private TextField firstNameText;
    private TextField middleNameText;
    private TextField phoneNumberText;

    private static Logger logger = Logger.getLogger(PatientWindow.class.getName());

    private static final String REQUIRED = "Обязательное для заполнения поле";

    PatientWindow(Table table, Object itemId) {
        this.table = table;
        this.itemId = itemId;
        createUI();
        processItem();
    }

    private void createUI() {

        setStyleName(AppStyle.MODAL_WINDOW);
        setWidth("450px");
        setHeight("350px");
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

        lastNameText = new TextField("Фамилия");
        lastNameText.setImmediate(true);
        lastNameText.setNullRepresentation("");
        lastNameText.setRequired(true);
        lastNameText.setRequiredError(REQUIRED);
        lastNameText.addValidator(new NullValidator(REQUIRED, false));
        lastNameText.setValidationVisible(false);
        lastNameText.setMaxLength(25);
        lastNameText.setWidth("100%");

        firstNameText = new TextField("Имя");
        firstNameText.setImmediate(true);
        firstNameText.setNullRepresentation("");
        firstNameText.setRequired(true);
        firstNameText.setRequiredError(REQUIRED);
        firstNameText.addValidator(new NullValidator(REQUIRED, false));
        firstNameText.setValidationVisible(false);
        firstNameText.setMaxLength(25);
        firstNameText.setWidth("100%");

        middleNameText = new TextField("Отчество");
        middleNameText.setImmediate(true);
        middleNameText.setNullRepresentation("");
        middleNameText.setRequired(true);
        middleNameText.setRequiredError(REQUIRED);
        middleNameText.addValidator(new NullValidator(REQUIRED, false));
        middleNameText.setValidationVisible(false);
        middleNameText.setMaxLength(25);
        middleNameText.setWidth("100%");

        phoneNumberText = new TextField("Номер телефона");
        phoneNumberText.setImmediate(true);
        phoneNumberText.setNullRepresentation("");
        phoneNumberText.setRequired(true);
        phoneNumberText.setRequiredError(REQUIRED);
        phoneNumberText.addValidator(new NullValidator(REQUIRED, false));
        phoneNumberText.setValidationVisible(false);
        phoneNumberText.setMaxLength(11);
        phoneNumberText.setWidth("100%");

        formLayout.addComponents(lastNameText, firstNameText, middleNameText, phoneNumberText);

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

    private void processItem() {
        if (itemId != null) {
            setCaption(" Редактирование пациента");
            if (table != null) {
                Item item = table.getItem(itemId);
                if (item != null) {
                    try {
                        String lastName = String.valueOf(item.getItemProperty("lastName").getValue());
                        String firstName = String.valueOf(item.getItemProperty("firstName").getValue());
                        String middleName = String.valueOf(item.getItemProperty("middleName").getValue());
                        String phoneNumber = String.valueOf(item.getItemProperty("phoneNumber").getValue());
                        lastNameText.setValue(lastName);
                        firstNameText.setValue(firstName);
                        middleNameText.setValue(middleName);
                        phoneNumberText.setValue(phoneNumber);
                    } catch (Exception e) {
                        logger.severe(e.getMessage());
                    }
                }
            }
        } else {
            setCaption(" Добавление пациента");
            lastNameText.focus();
        }

        okButton.addClickListener(clickEvent -> {
                try {
                    Patient patient = new Patient();
                    patient.setLastName(lastNameText.getValue());
                    patient.setFirstName(firstNameText.getValue());
                    patient.setMiddleName(middleNameText.getValue());
                    patient.setPhoneNumber(phoneNumberText.getValue());
                    PatientDAO patientDAO = DAOFactory.getInstance().getPatientDAO();
                    if (itemId != null) {
                        Long id = (Long) itemId;
                        patient.setId(id);
                        patientDAO.update(patient);
                        if (table != null) {
                            Item item = table.getItem(itemId);
                            if (item != null) {
                                item.getItemProperty("lastName").setValue(patient.getLastName());
                                item.getItemProperty("firstName").setValue(patient.getFirstName());
                                item.getItemProperty("middleName").setValue(patient.getMiddleName());
                                item.getItemProperty("phoneNumber").setValue(patient.getPhoneNumber());
                            }
                        }
                    } else {
                        patient = patientDAO.persist(patient);
                        if (patient != null && patient.getId() != null) {
                            if (table != null) {
                                Object itemId = table.addItem(new Object[]{
                                        patient.getLastName(), patient.getFirstName(), patient.getMiddleName(), patient.getPhoneNumber()}, patient.getId());
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
        });

        cancelButton.addClickListener(clickEvent -> close());
    }
}
