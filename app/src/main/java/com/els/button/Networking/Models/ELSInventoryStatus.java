package com.els.button.Networking.Models;

/**
 * Created by Cam on 1/24/17.
 */


public class ELSInventoryStatus {

    private String title;
    private String description;
    private String statusSheet;
    private ELSInventoryStatusAction action;
    private ELSInventoryStatusAppearance appearance;

    public ELSInventoryStatus(String title, String description, String statusSheet, ELSInventoryStatusAction action, ELSInventoryStatusAppearance appearance) {
        this.title = title;
        this.description = description;
        this.statusSheet = statusSheet;
        this.action = action;
        this.appearance = appearance;
    }

    public ELSInventoryStatusAction getAction() {
        return action;
    }

    public void setAction(ELSInventoryStatusAction action) {
        this.action = action;
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
