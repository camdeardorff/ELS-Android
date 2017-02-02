package com.els.button.Models;

import android.util.Log;

import com.els.button.Database.AppDatabase;
import com.els.button.Networking.Models.ELSInventoryStatus;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by cameron on 4/5/16.
 */
@Table(database = AppDatabase.class)
public class ELSLimri extends ELSEntity {

    @Column
    public String statusSheet;

    @ForeignKey(tableClass = ELSLimriButton.class)
    public ELSLimriButton button;

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

    @Override
    public void updateStatus(ELSInventoryStatus status) {
        Log.d("ELSLimri", "updateStatus");

        super.updateStatus(status);
        if (status.getStatusSheet() != null && !status.getStatusSheet().equals("")) {
            this.setStatusSheet(status.getStatusSheet());
        }

        if (status.getAppearance() != null && status.getAction() != null) {
            this.getButton().updateAction(status.getAction());
            this.getButton().updateAppearance(status.getAppearance());
        }

        this.save();
        Log.d("ELSLimri", "after updateStatus: status sheet: " + this.getStatusSheet());

    }

    public String getStatusSheet() {
        return statusSheet;
    }

    public void setStatusSheet(String statusSheet) {
        this.statusSheet = statusSheet;
    }


    public ELSLimriButton getButton() {
        return button;
    }

    public void setButton(ELSLimriButton button) {
        Log.d("ELSLimri", "set button with button: " + button.getTitle());
        this.button = button;
    }
}
