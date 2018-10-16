package com.haulmont.testtask.app;


import com.haulmont.testtask.dao.DAOException;
import com.haulmont.testtask.dao.DAOFactory;
import com.haulmont.testtask.entity.Patient;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;

import java.util.List;
import java.util.logging.Logger;

public class PatientsView extends VerticalLayout implements View {
    static final String NAME = "patients";

    private Table table;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    private static Logger logger = Logger.getLogger(PatientsView.class.getName());

    PatientsView() {
        createUI();
        processEvents();
    }

    private void createUI() {
        try {
            table = new Table();
            table.addContainerProperty("lastName", String.class, null,
                    "Фамилия", null, Table.Align.LEFT);
            table.addContainerProperty("firstName", String.class, null,
                    "Имя", null, Table.Align.LEFT);
            table.addContainerProperty("middleName", String.class, null,
                    "Отчество", null, Table.Align.LEFT);
            table.addContainerProperty("phoneNumber", String.class, null,
                    "Номер телефона", null, Table.Align.LEFT);
            table.setColumnWidth("lastName", 200);
            table.setColumnWidth("firstName", 200);
            table.setColumnWidth("middleName", 200);
            table.setColumnExpandRatio("phoneNumber", 1f);
            table.setSelectable(true);
            table.setImmediate(true);
            table.setNullSelectionAllowed(false);
            table.setSizeFull();

            HorizontalLayout buttonsLayout = new HorizontalLayout();
            buttonsLayout.setSpacing(true);

            addButton = new Button("Добавить", new ThemeResource(AppStyle.BUTTON_ADD));
            editButton = new Button("Изменить", new ThemeResource(AppStyle.BUTTON_EDIT));
            deleteButton = new Button("Удалить", new ThemeResource(AppStyle.BUTTON_DELETE));
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            buttonsLayout.addComponents(addButton, editButton, deleteButton);

            setMargin(true);
            setSpacing(true);
            setSizeFull();
            addComponents(table, buttonsLayout);
            setExpandRatio(table, 1f);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void processEvents() {
        try {
            table.addValueChangeListener(valueChangeEvent -> {
                if (table.getValue() != null) {
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                } else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            });

            table.addItemClickListener(itemClickEvent -> {
                if (itemClickEvent.isDoubleClick() &&
                        itemClickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
                    Object itemId = itemClickEvent.getItemId();
                    if (itemId != null) {
                        getUI().addWindow(new PatientWindow(table, itemId));
                    }
                }
            });

            addButton.addClickListener(clickEvent ->
                    getUI().addWindow(new PatientWindow(table, null)));

            editButton.addClickListener(clickEvent -> {
                Object itemId = table.getValue();
                if (itemId != null) {
                    getUI().addWindow(new PatientWindow(table, itemId));
                }
            });

            deleteButton.addClickListener(clickEvent -> {
                Object itemId = table.getValue();
                if (itemId != null) {
                    Object selItemId = table.prevItemId(itemId);
                    if (selItemId == null) {
                        selItemId = table.nextItemId(itemId);
                    }
                    Patient patient = new Patient();
                    Long id = (Long) itemId;
                    patient.setId(id);
                    try {
                        DAOFactory.getInstance().getPatientDAO().delete(patient);
                        table.removeItem(itemId);
                        if (selItemId != null) {
                            table.select(selItemId);
                            table.setCurrentPageFirstItemId(selItemId);
                        }
                    } catch (DAOException e) {
                        if (e.getCause().getClass().equals(java.sql.SQLIntegrityConstraintViolationException.class)) {
                            Notification notification = new Notification("Удаление пациента невозможно, так как у него есть рецепты",
                                    Notification.Type.HUMANIZED_MESSAGE);
                            notification.setStyleName(AppStyle.THEME_NAME);
                            notification.show(Page.getCurrent());
                        } else {
                            logger.severe(e.getMessage());
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void fillTable() {
        try {
            List<Patient> patients = DAOFactory.getInstance().getPatientDAO().getAll();
            table.removeAllItems();
            for (Patient patient : patients) {
                table.addItem(new Object[] {patient.getLastName(), patient.getFirstName(), patient.getMiddleName(), patient.getPhoneNumber()}, patient.getId());
            }
            table.sort(new Object[] {"lastName", "firstName", "middleName", "phoneNumber"}, new boolean[] {true, true, true, true});
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        fillTable();
    }
}
