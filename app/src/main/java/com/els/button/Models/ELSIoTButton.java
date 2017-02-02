package com.els.button.Models;


import com.els.button.Database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Cam on 1/10/17.
 */
@Table(database = AppDatabase.class)
public class ELSIoTButton extends BaseModel {

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    private String value;

    @Column
    private String title;

    @ForeignKey(tableClass = ELSIoT.class, stubbedRelationship = true)
    public ELSIoT container;

    public ELSIoTButton() {
        this.value = "";
        this.title = "";
    }

    public ELSIoTButton(String value, String title) {
        this.value = value;
        this.title = title;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ELSIoT getContainer() {
        return container;
    }

    public void setContainer(ELSIoT container) {
        this.container = container;
    }
}
