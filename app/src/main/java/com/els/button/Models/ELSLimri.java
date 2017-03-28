package com.els.button.Models;

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

        super.updateStatus(status);
        if (status.getStatusSheet() != null && !status.getStatusSheet().equals("")) {
            this.setStatusSheet(status.getStatusSheet());
        }

        this.save();
    }


    public void updateStatus(String hostIp, final StatusUpdateResult callback) {

        final ELSLimri context = this;
        ELSRest rest = new ELSRest(hostIp, this.getInventoryID(), this.getPin());
        rest.getInventoryStatus(getStatusSheet(), new ELSRestInventoryStatusRequestCallback() {
            @Override
            public void onSuccess(ELSInventoryStatus inventoryStatus) {
                ELSLimri.super.updateStatus(inventoryStatus);
                if (inventoryStatus.getStatusSheet() != null && !inventoryStatus.getStatusSheet().equals("")) {
                    context.setStatusSheet(inventoryStatus.getStatusSheet());
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


    public ELSLimriButton getButton() {
        return button;
    }

    public void setButton(ELSLimriButton button) {
        this.button = button;
    }
}
