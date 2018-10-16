package com.haulmont.testtask.app;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;

public class MainView extends CustomLayout implements View {

    static final String NAME = "";

    MainView() {
        setTemplateName(AppStyle.TEMPLATE);
        setStyleName(AppStyle.MAIN_LAYOUT);
        setSizeFull();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
