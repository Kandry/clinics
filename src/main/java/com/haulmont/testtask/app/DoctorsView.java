package com.haulmont.testtask.app;


import com.haulmont.testtask.dao.DAOException;
import com.haulmont.testtask.dao.DAOFactory;
import com.haulmont.testtask.entity.Doctor;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;

import java.util.List;
import java.util.logging.Logger;

public class DoctorsView extends VerticalLayout implements View {
    static final String NAME = "doctors";

    //Отображение статистической информации по количеству рецептов, выписанных врачами

    private Table table;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button statisticsButton;

    private static Logger logger = Logger.getLogger(DoctorsView.class.getName());

    DoctorsView() {
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
            table.addContainerProperty("specialization", String.class, null,
                    "Специализация", null, Table.Align.LEFT);
            table.setColumnWidth("lastName", 200);
            table.setColumnWidth("firstName", 200);
            table.setColumnWidth("middleName", 200);
            table.setColumnExpandRatio("specialization", 1f);
            table.setSelectable(true);
            table.setImmediate(true);
            table.setNullSelectionAllowed(false);
            table.setSizeFull();

            HorizontalLayout buttonsLayout = new HorizontalLayout();
            buttonsLayout.setSpacing(true);

            addButton = new Button("Добавить", new ThemeResource(AppStyle.BUTTON_ADD));
            editButton = new Button("Изменить", new ThemeResource(AppStyle.BUTTON_EDIT));
            deleteButton = new Button("Удалить", new ThemeResource(AppStyle.BUTTON_DELETE));
            statisticsButton = new Button("Показать статистику", new ThemeResource(AppStyle.BUTTON_STATISTICS));
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            statisticsButton.setEnabled(false);
            buttonsLayout.addComponents(addButton, editButton, deleteButton, statisticsButton);

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
                    statisticsButton.setEnabled(true);
                } else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    statisticsButton.setEnabled(false);
                }
            });

            table.addItemClickListener(itemClickEvent -> {
                if (itemClickEvent.isDoubleClick() &&
                        itemClickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
                    Object itemId = itemClickEvent.getItemId();
                    if (itemId != null) {
                        getUI().addWindow(new DoctorWindow(table, itemId));
                    }
                }
            });

            addButton.addClickListener(clickEvent ->
                    getUI().addWindow(new DoctorWindow(table, null)));

            editButton.addClickListener(clickEvent -> {
                Object itemId = table.getValue();
                if (itemId != null) {
                    getUI().addWindow(new DoctorWindow(table, itemId));
                }
            });

            deleteButton.addClickListener(clickEvent -> {
                Object itemId = table.getValue();
                if (itemId != null) {
                    Object selItemId = table.prevItemId(itemId);
                    if (selItemId == null) {
                        selItemId = table.nextItemId(itemId);
                    }
                    Doctor doctor = new Doctor();
                    Long id = (Long) itemId;
                    doctor.setId(id);
                    try {
                        DAOFactory.getInstance().getDoctorDAO().delete(doctor);
                        table.removeItem(itemId);
                        if (selItemId != null) {
                            table.select(selItemId);
                            table.setCurrentPageFirstItemId(selItemId);
                        }
                    } catch (DAOException e) {
                        if (e.getCause().getClass().equals(java.sql.SQLIntegrityConstraintViolationException.class)) {
                            Notification notification = new Notification("Удаление врача невозможно, так как у него есть рецепты",
                                    Notification.Type.HUMANIZED_MESSAGE);
                            notification.setStyleName(AppStyle.THEME_NAME);
                            notification.show(Page.getCurrent());
                        } else {
                            logger.severe(e.getMessage());
                        }
                    }
                }
            });

            statisticsButton.addClickListener(clickEvent ->
            {
                Object itemId = table.getValue();
                if (itemId != null) {
                    Doctor doctor = new Doctor();
                    Long id = (Long) itemId;
                    doctor.setId(id);
                    try {
                        int stat = DAOFactory.getInstance().getDoctorDAO().countPrescriptions(doctor);
                        Notification notification = new Notification("Количество рецептов, выписанных данным врачом: "+stat, Notification.Type.HUMANIZED_MESSAGE);
                        notification.setStyleName(AppStyle.THEME_NAME);
                        notification.show(Page.getCurrent());
                    }catch (DAOException e){
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void fillTable() {
        try {
            List<Doctor> doctors = DAOFactory.getInstance().getDoctorDAO().getAll();
            table.removeAllItems();
            for (Doctor doctor : doctors) {
                table.addItem(new Object[] {doctor.getLastName(), doctor.getFirstName(), doctor.getMiddleName(), doctor.getSpecialization()}, doctor.getId());
            }
            table.sort(new Object[] {"lastName", "firstName", "middleName", "specialization"}, new boolean[] {true, true, true, true});
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        fillTable();
    }
}
