package com.haulmont.testtask.app;

import com.vaadin.event.FieldEvents;
import com.vaadin.ui.TextField;

public class IntegerField extends TextField implements FieldEvents.TextChangeListener {
    String lastValue;

    public IntegerField(String caption) {
        setImmediate(true);
        setTextChangeEventMode(TextChangeEventMode.EAGER);
        addTextChangeListener(this);
        this.setCaption(caption);
    }

    @Override
    public void textChange(FieldEvents.TextChangeEvent event) {
        String text = event.getText();
        try {
            new Integer(text);
            lastValue = text;
        } catch (NumberFormatException e) {
            setValue(lastValue);
        }
    }
}
