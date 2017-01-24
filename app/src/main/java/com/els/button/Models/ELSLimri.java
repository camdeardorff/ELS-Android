package com.els.button.Models;

import com.els.button.Database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by cameron on 4/5/16.
 */
@Table(database = AppDatabase.class)
public class ELSLimri extends ELSEntity {

    @Column
    public String statusSheet;

    public ELSLimri() {
        super();
        this.statusSheet = "";
    }

    public ELSLimri(String inventoryID, String pin, String statusSheet) {
        super(inventoryID, pin);
        this.statusSheet = statusSheet;
    }

    public ELSLimri(String inventoryID, String pin, String title, String description, String statusSheet) {
        super(inventoryID, pin, title, description);
        this.statusSheet = statusSheet;
    }

    public String getStatusSheet() {
        return statusSheet;
    }

    public void setStatusSheet(String statusSheet) {
        this.statusSheet = statusSheet;
    }
}
