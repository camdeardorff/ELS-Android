package com.els.button.Models;

import android.util.Log;

import com.els.button.Database.AppDatabase;
import com.els.button.Interfaces.StatusUpdateResult;
import com.els.button.Networking.Callbacks.ELSRestInventoryStatusRequestCallback;
import com.els.button.Networking.ELSRest;
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

    @Column
    public ELSLimriIcon icon;

    @ForeignKey(tableClass = ELSLimriButton.class)
    public ELSLimriButton button;

    public ELSLimri() {
        super();
        this.statusSheet = "";
    }

    public ELSLimri(String serverLocation, String inventoryID, String pin, String statusSheet) {
        super(serverLocation, inventoryID, pin);
        this.statusSheet = statusSheet;
        this.icon = ELSLimriIcon.NONE;
    }

    public ELSLimri(String serverLocation, String inventoryID, String pin, String title, String description, String statusSheet, ELSLimriIcon icon) {
        super(serverLocation, inventoryID, pin, title, description);
        this.statusSheet = statusSheet;
        this.icon = icon;
    }

    @Override
    public void updateStatus(ELSInventoryStatus status) {
        Log.d("ELSLimri", "Update status 1 with icon: " + status.getAppearance().getIcon());
        super.updateStatus(status);
        if (status.getStatusSheet() != null && !status.getStatusSheet().equals("")) {
            this.setStatusSheet(status.getStatusSheet());
        }
        if (status.getAppearance().getIcon() != null) {
            this.setIcon(status.getAppearance().getIcon());
        }

        this.save();
    }


    public void updateStatus(final StatusUpdateResult callback) {

        final ELSLimri context = this;
        ELSRest rest = new ELSRest(this.getServerLocation(), this.getInventoryID(), this.getPin());
        rest.getInventoryStatus(getStatusSheet(), new ELSRestInventoryStatusRequestCallback() {
            @Override
            public void onSuccess(ELSInventoryStatus inventoryStatus) {
                ELSLimri.super.updateStatus(inventoryStatus);
                Log.d("ELSLimri", "Update status 2 with icon: " + inventoryStatus.getAppearance().getIcon());


                if (inventoryStatus.getStatusSheet() != null && !inventoryStatus.getStatusSheet().equals("")) {
                    context.setStatusSheet(inventoryStatus.getStatusSheet());
                }
                if (inventoryStatus.getAppearance().getIcon() != null) {
                    Log.d("ELSLimri", "set icon because it is not null");
                    context.setIcon(inventoryStatus.getAppearance().getIcon());
                }

                if (inventoryStatus.getAppearance() != null && inventoryStatus.getActions() != null && inventoryStatus.getActions().size() > 0) {
                    context.getButton().updateAction(inventoryStatus.getActions().get(0));
                    context.getButton().setActions(inventoryStatus.getActions());
                    context.getButton().updateAppearance(inventoryStatus.getAppearance());
                }
                context.save();
                callback.success();
            }

            @Override
            public void onFailure(Boolean didFailLogin) {

                if (didFailLogin) {
                    // fix credentials
                    callback.failureFromCredentials();
                } else {
                    // could not connect to server
                    callback.failureFromConnectivity();
                }
            }
        });
    }

    public String getStatusSheet() {
        return statusSheet;
    }

    public void setStatusSheet(String statusSheet) {
        this.statusSheet = statusSheet;
    }

    public ELSLimriIcon getIcon() {
        return icon;
    }

    public void setIcon(ELSLimriIcon icon) {
        Log.d("ELSLimri", "set icon now");
        this.icon = icon;
    }

    public ELSLimriButton getButton() {
        return button;
    }

    public void setButton(ELSLimriButton button) {
        this.button = button;
    }
}
