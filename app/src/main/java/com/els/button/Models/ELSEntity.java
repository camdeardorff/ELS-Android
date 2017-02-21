package com.els.button.Models;

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
    public String inventoryID;
    @Column
    public String pin;
    @Column
    public String title = "";
    @Column
    public String description = "";
    @Column
    public Date dateAdded;

    public ELSEntity() {
        this.inventoryID = "";
        this.pin = "";
        this.dateAdded = new Date();
    }

    public ELSEntity(String inventoryID, String pin) {
        this.inventoryID = inventoryID;
        this.pin = pin;
        this.dateAdded = new Date();
    }

    public ELSEntity(String inventoryID, String pin, String title, String description) {
        this.inventoryID = inventoryID;
        this.pin = pin;
        this.title = title;
        this.description = description;
        this.dateAdded = new Date();
    }

    public ELSEntity(String inventoryID, String pin, String title, String description, Date dateAdded) {
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
        this.save();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getInventoryID() {

        return inventoryID;
    }

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}