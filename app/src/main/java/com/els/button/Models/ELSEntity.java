package com.els.button.Models;

import android.util.Log;

import com.els.button.Database.AppDatabase;
import com.els.button.Networking.Models.ELSInventoryStatus;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Created by cameron on 4/5/16.
 */

@Table(database = AppDatabase.class)
public class ELSEntity extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String serverLocation;
    @Column
    public String inventoryID;
    @Column
    public String pin;
    @Column
    public String title = "";
    @Column
    public Date dateAdded;

    // don't save, get fresh each time
    public String description = "";

    public ELSEntity() {
        this.serverLocation = "";
        this.inventoryID = "";
        this.pin = "";
        this.dateAdded = new Date();
    }

    public ELSEntity(String serverLocation, String inventoryID, String pin) {
        this.serverLocation = serverLocation;
        this.inventoryID = inventoryID;
        this.pin = pin;
        this.dateAdded = new Date();
    }

    public ELSEntity(String serverLocation, String inventoryID, String pin, String title, String description) {
        this.serverLocation = serverLocation;
        this.inventoryID = inventoryID;
        this.pin = pin;
        this.title = title;
        this.description = description;
        this.dateAdded = new Date();
    }

    public ELSEntity(String serverLocation, String inventoryID, String pin, String title, String description, Date dateAdded) {
        this.serverLocation = serverLocation;
        this.inventoryID = inventoryID;
        this.pin = pin;
        this.title = title;
        this.description = description;
        this.dateAdded = dateAdded;
    }

    public void updateStatus(ELSInventoryStatus status) {
        if (status.getTitle() != null && status.getTitle() != "") {
            this.setTitle(status.getTitle());
        }
        if (status.getDescription() != null && status.getDescription() != "") {
            this.setDescription(status.getDescription());
        }
        if (status.getServerLocation() != null && status.getServerLocation() != "") {
            this.setServerLocation(status.getServerLocation());
        }
        this.save();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerLocation() {
        return serverLocation;
    }

    public void setServerLocation(String serverLocation) {
        this.serverLocation = serverLocation;
        Log.d("ELSEntity", "set server location to " + serverLocation);
    }

    public String getInventoryID() {

        return inventoryID;
    }

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}