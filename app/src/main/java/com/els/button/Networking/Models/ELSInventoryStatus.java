package com.els.button.Networking.Models;

import java.util.ArrayList;

/**
 * Created by Cam on 1/24/17.
 */


public class ELSInventoryStatus {

    private String title;
    private String description;
    private String statusSheet;
    private ArrayList<ELSInventoryStatusAction> actions;
    private ELSInventoryStatusAppearance appearance;

    public ELSInventoryStatus(String title, String description, String statusSheet, ArrayList<ELSInventoryStatusAction> actions, ELSInventoryStatusAppearance appearance) {
        this.title = title;
        this.description = description;
        this.statusSheet = statusSheet;
        this.actions = actions;
        this.appearance = appearance;
    }

    public ArrayList<ELSInventoryStatusAction> getActions() {
        return actions;
    }

    public void setActions(ArrayList<ELSInventoryStatusAction> actions) {
        this.actions = actions;
    }

    public ELSInventoryStatusAppearance getAppearance() {
        return appearance;
    }

    public void setAppearance(ELSInventoryStatusAppearance appearance) {
        this.appearance = appearance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatusSheet() {
        return statusSheet;
    }

    public void setStatusSheet(String statusSheet) {
        this.statusSheet = statusSheet;
    }

}
