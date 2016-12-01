package com.els.button.Models;

/**
 * Created by cameron on 4/5/16.
 */
public class ELSLimri extends ELSEntity {
    public String statusSheet;

    public ELSLimri(String inventoryID, String pin, String statusSheet) {
        super(inventoryID, pin);
        this.statusSheet = statusSheet;
    }

    public ELSLimri(String inventoryID, String pin, String title, String description, String statusSheet) {
        super(inventoryID, pin, title, description);
        this.statusSheet = statusSheet;
    }







}
