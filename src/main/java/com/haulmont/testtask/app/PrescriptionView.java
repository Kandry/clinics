package com.haulmont.testtask.app;

import com.haulmont.testtask.dao.DAOException;
import com.haulmont.testtask.dao.DAOFactory;
import com.haulmont.testtask.entity.Doctor;
import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.entity.Prescription;
import com.vaadin.data.Container;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class PrescriptionView extends VerticalLayout implements View{

    static final String NAME = "prescriptions";

    private Table table;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private TextField descriptionText;
    private TextField priorityText;
    private TextField patientText;
    private Button filterButton;

    private static Logger logger = Logger.getLogger(PrescriptionView.class.getName());

    PrescriptionView() {
        createUI();
        processEvents();
    }

    private void createUI() {
        try {
            Panel filterPanel = new Panel("Фильтр");
            HorizontalLayout filterLayout = new HorizontalLayout();
            filterLayout.setMargin(true);
            filterLayout.setSpacing(true);

            descriptionText = new TextField();
            priorityText = new TextField();
            patientText = new TextField();
            descriptionText.setInputPrompt("Описание");
            priorityText.setInputPrompt("Приоритет");
            patientText.setInputPrompt("Пациент");
            filterButton = new Button("Применить");

            filterLayout.addComponents(descriptionText, priorityText, patientText, filterButton);
            filterPanel.setContent(filterLayout);

            table = new Table();
            table.addContainerProperty("description", String.class, null,
                    "Описание", null, Table.Align.LEFT);
            table.addContainerProperty("patient", Patient.class, null,
                    "Пациент", null, Table.Align.LEFT);
            table.addContainerProperty("doctor", Doctor.class, null,
                    "Врач", null, Table.Align.LEFT);
            table.addContainerProperty("createDate", Date.class, null,
                    "Дата создания", null, Table.Align.LEFT);
            table.addContainerProperty("expiration", Integer.class, null,
                    "Срок действия в днях", null, Table.Align.LEFT);
            table.addContainerProperty("priority", String.class, null,
                    "Приоритет", null, Table.Align.LEFT);
            table.setColumnWidth("description", 150);
            table.setColumnWidth("patient", 300);
            table.setColumnWidth("doctor", 300);
            table.setColumnWidth("createDate", 160);
            table.setColumnWidth("expiration", 220);
            table.setColumnExpandRatio("priority", 1f);
            table.setSelectable(true);
            table.setImmediate(true);
            table.setNullSelectionAllowed(false);
            table.setSizeFull();

            table.setConverter("createDate", new StringToDateConverter() {
                @Override
                public DateFormat getFormat(Locale locale) {
                    return new SimpleDateFormat("dd.MM.yyyy");
                }
            });

            table.setConverter("patient", new PatientConverter());
            table.setConverter("doctor", new DoctorConverter());

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
            addComponents(filterPanel, table, buttonsLayout);
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
                        getUI().addWindow(new PrescriptionWindow(table, itemId));
                    }
                }
            });

            addButton.addClickListener(clickEvent ->
                    getUI().addWindow(new PrescriptionWindow(table, null)));

            editButton.addClickListener(clickEvent -> {
                Object itemId = table.getValue();
                if (itemId != null) {
                    getUI().addWindow(new PrescriptionWindow(table, itemId));
                }
            });

            deleteButton.addClickListener(clickEvent -> {
                Object itemId = table.getValue();
                if (itemId != null) {
                    Object selItemId = table.prevItemId(itemId);
                    if (selItemId == null) {
                        selItemId = table.nextItemId(itemId);
                    }
                    Prescription prescription = new Prescription();
                    Long id = (Long) itemId;
                    prescription.setId(id);
                    try {
                        DAOFactory.getInstance().getPrescriptionDAO().delete(prescription);
                        table.removeItem(itemId);
                        if (selItemId != null) {
                            table.select(selItemId);
                            table.setCurrentPageFirstItemId(selItemId);
                        }
                    } catch (DAOException e) {
                        logger.severe(e.getMessage());
                    }
                }
            });

            filterButton.addClickListener(clickEvent -> {
                String description = descriptionText.getValue().trim();
                String priority = priorityText.getValue().trim();
                String patient = patientText.getValue().trim();

                Container.Filterable filterContainer = (Container.Filterable) table.getContainerDataSource();
                filterContainer.removeAllContainerFilters();

                SimpleStringFilter descriptionFilter = new SimpleStringFilter("description", description, true, false);
                SimpleStringFilter priorityFilter = new SimpleStringFilter("priority", priority, true, false);
                PatientFilter patientFilter = new PatientFilter("patient", patient, true);
                filterContainer.addContainerFilter(new And(descriptionFilter, priorityFilter, patientFilter));
            });

        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void fillTable() {
        try {
            List<Prescription> prescriptions = DAOFactory.getInstance().getPrescriptionDAO().getAll();
            table.removeAllItems();
            for (Prescription prescription : prescriptions) {
                table.addItem(new Object[] {prescription.getDescription(), prescription.getPatient(), prescription.getDoctor(),
                        prescription.getCreateDate(), prescription.getExpiration(), prescription.getPriority()}, prescription.getId());
            }
            table.sort(new Object[] {"description","createDate","expiration"}, new boolean[] {true, true, true});
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        fillTable();
    }

}