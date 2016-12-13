package com.els.button.Models;

/**
 * Created by cameron on 4/5/16.
 */
public class ELSEntity {
    public String inventoryID;
    public String pin;
    public String title = "";
    public String description = "";

    public ELSEntity(String inventoryID, String pin) {
        this.inventoryID = inventoryID;
        this.pin = pin;
    }

    public ELSEntity(String inventoryID, String pin, String title, String description) {
        this.inventoryID = inventoryID;
        this.pin = pin;
        this.title = title;
        this.description = description;
    }


}