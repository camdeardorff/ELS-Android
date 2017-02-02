package com.els.button.Networking.Models;

import com.els.button.Models.ELSLimriButtonPressAction;

/**
 * Created by Cam on 1/24/17.
 */

public class ELSInventoryStatusAction {

    private ELSLimriButtonPressAction type;
    private String location;

    public ELSInventoryStatusAction(ELSLimriButtonPressAction type, String location) {
        this.type = type;
        this.location = location;
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
}
