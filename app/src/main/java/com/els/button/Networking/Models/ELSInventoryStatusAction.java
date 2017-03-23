package com.els.button.Networking.Models;

import com.els.button.Models.ELSLimriButtonPressAction;

/**
 * Created by Cam on 1/24/17.
 */

public class ELSInventoryStatusAction {

    private ELSLimriButtonPressAction type;
    private String location;
    private Boolean display;
    private String value;

    public ELSInventoryStatusAction(ELSLimriButtonPressAction type, String location, String value, Boolean display) {
        this.type = type;
        this.location = location;
        this.display = display;
        //TODO: get this value from content server
        this.value = value;
    }

    public ELSLimriButtonPressAction getType() {
        return type;
    }

    public void setType(ELSLimriButtonPressAction type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
