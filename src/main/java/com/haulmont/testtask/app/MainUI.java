package com.haulmont.testtask.app;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

@Theme(AppStyle.THEME_NAME)
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        String title = "Поликлинника";
        getPage().setTitle(title);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(false);
        mainLayout.setSpacing(false);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setHeight("48px");
        headerLayout.setWidth("100%");
        headerLayout.setMargin(false);
        headerLayout.setSpacing(false);

        Button mainButton = new Button("Главная");
        mainButton.setHeight("100%");
        mainButton.addStyleName(AppStyle.BORDERLESS);

        Button patientsButton = new Button("Пациенты");
        patientsButton.setHeight("100%");
        patientsButton.addStyleName(AppStyle.BORDERLESS);

        Button doctorsButton = new Button("Врачи");
        doctorsButton.setHeight("100%");
        doctorsButton.addStyleName(AppStyle.BORDERLESS);

        Button prescriptionsButton = new Button("Рецепты");
        prescriptionsButton.setHeight("100%");
        prescriptionsButton.addStyleName(AppStyle.BORDERLESS);

        Label header = new Label(title);
        header.setWidth(null);

        Embedded logo = new Embedded(null, new ThemeResource(AppStyle.HEADER_LOGO));
        logo.setHeight("34px");

        headerLayout.addComponents(logo, header);
        headerLayout.setComponentAlignment(header, Alignment.MIDDLE_LEFT);
        headerLayout.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);
        headerLayout.setExpandRatio(header, 1f);

        headerLayout.addComponents(mainButton, patientsButton, doctorsButton, prescriptionsButton);
        headerLayout.setSpacing(true);

        VerticalLayout viewsLayout = new VerticalLayout();
        viewsLayout.setSizeFull();
        viewsLayout.setMargin(false);
        viewsLayout.setSpacing(true);

        mainLayout.addComponents(headerLayout, viewsLayout);
        mainLayout.setExpandRatio(viewsLayout, 1f);

        ViewDisplay viewDisplay = new Navigator.ComponentContainerViewDisplay(viewsLayout);
        Navigator navigator = new Navigator(this, viewDisplay);
        navigator.addView(MainView.NAME, new MainView());
        navigator.addView(PatientsView.NAME, new PatientsView());
        navigator.addView(DoctorsView.NAME, new DoctorsView());
        navigator.addView(PrescriptionView.NAME, new PrescriptionView());

        mainButton.addClickListener(clickEvent -> navigator.navigateTo(MainView.NAME));
        patientsButton.addClickListener(clickEvent -> navigator.navigateTo(PatientsView.NAME));
        doctorsButton.addClickListener(clickEvent -> navigator.navigateTo(DoctorsView.NAME));
        prescriptionsButton.addClickListener(clickEvent -> navigator.navigateTo(PrescriptionView.NAME));

        headerLayout.setStyleName(AppStyle.HEADER_LAYOUT);
        mainLayout.setStyleName(AppStyle.MAIN_LAYOUT);
        viewsLayout.setStyleName(AppStyle.VIEW_LAYOUT);

        setContent(mainLayout);
    }
}