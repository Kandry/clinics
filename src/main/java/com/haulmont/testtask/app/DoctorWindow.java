package com.haulmont.testtask.app;

import com.haulmont.testtask.dao.DAOFactory;
import com.haulmont.testtask.dao.DoctorDAO;
import com.haulmont.testtask.entity.Doctor;
import com.vaadin.data.Item;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.ui.*;

import java.util.logging.Logger;

public class DoctorWindow extends Window {
    private Table table;
    private Object itemId;
    private Button okButton;
    private Button cancelButton;
    private TextField lastNameText;
    private TextField firstNameText;
    private TextField middleNameText;
    private TextField specializationText;

    private static Logger logger = Logger.getLogger(DoctorWindow.class.getName());

    private static final String REQUIRED = "Обязательное для заполнения поле";

    DoctorWindow(Table table, Object itemId) {
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

        specializationText = new TextField("Специализация");
        specializationText.setImmediate(true);
        specializationText.setNullRepresentation("");
        specializationText.setRequired(true);
        specializationText.setRequiredError(REQUIRED);
        specializationText.addValidator(new NullValidator(REQUIRED, false));
        specializationText.setValidationVisible(false);
        specializationText.setMaxLength(40);
        specializationText.setWidth("100%");

        formLayout.addComponents(lastNameText, firstNameText, middleNameText, specializationText);

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
            setCaption(" Редактирование врача");
            if (table != null) {
                Item item = table.getItem(itemId);
                if (item != null) {
                    try {
                        String lastName = String.valueOf(item.getItemProperty("lastName").getValue());
                        String firstName = String.valueOf(item.getItemProperty("firstName").getValue());
                        String middleName = String.valueOf(item.getItemProperty("middleName").getValue());
                        String specialization = String.valueOf(item.getItemProperty("specialization").getValue());
                        lastNameText.setValue(lastName);
                        firstNameText.setValue(firstName);
                        middleNameText.setValue(middleName);
                        specializationText.setValue(specialization);
                    } catch (Exception e) {
                        logger.severe(e.getMessage());
                    }
                }
            }
        } else {
            setCaption(" Добавление врача");
            lastNameText.focus();
        }

        okButton.addClickListener(clickEvent -> {
                try {
                    Doctor doctor = new Doctor();
                    doctor.setLastName(lastNameText.getValue());
                    doctor.setFirstName(firstNameText.getValue());
                    doctor.setMiddleName(middleNameText.getValue());
                    doctor.setSpecialization(specializationText.getValue());
                    DoctorDAO doctorDAO = DAOFactory.getInstance().getDoctorDAO();
                    if (itemId != null) {
                        Long id = (Long) itemId;
                        doctor.setId(id);
                        doctorDAO.update(doctor);
                        if (table != null) {
                            Item item = table.getItem(itemId);
                            if (item != null) {
                                item.getItemProperty("lastName").setValue(doctor.getLastName());
                                item.getItemProperty("firstName").setValue(doctor.getFirstName());
                                item.getItemProperty("middleName").setValue(doctor.getMiddleName());
                                item.getItemProperty("specialization").setValue(doctor.getSpecialization());
                            }
                        }
                    } else {
                        doctor = doctorDAO.persist(doctor);
                        if (doctor != null && doctor.getId() != null) {
                            if (table != null) {
                                Object itemId = table.addItem(new Object[]{
                                        doctor.getLastName(), doctor.getFirstName(), doctor.getMiddleName(), doctor.getSpecialization()}, doctor.getId());
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
