package com.els.button.Models;

import com.els.button.Database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by cameron on 4/5/16.
 */
@Table(database = AppDatabase.class)
public class ELSIoT extends ELSEntity {

    @Column
    public String qID;

    public List<ELSIoTButton> buttons;

    public ELSIoT() {
        this.qID = "";
        this.buttons = null;
    }

    public ELSIoT(String serverLocation, String inventoryID, String pin, String qID, List<ELSIoTButton> buttons) {
        super(serverLocation, inventoryID, pin);
        this.qID = qID;
        this.buttons = buttons;
        associateButtons();
    }

    public ELSIoT(String serverLocation, String inventoryID, String pin, String qID, String title, String description, List<ELSIoTButton> buttons) {
        super(serverLocation, inventoryID, pin, title, description);
        this.qID = qID;
        this.buttons = buttons;
        associateButtons();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getqID() {
        return qID;
    }

    public void setqID(String qID) {
        this.qID = qID;
    }


    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "buttons")
    public List<ELSIoTButton> getButtons() {
        if (buttons == null || buttons.isEmpty()) {
            buttons = SQLite.select()
                    .from(ELSIoTButton.class)
                    .where(ELSIoTButton_Table.container_id.is(id))
                    .queryList();
        }
        return buttons;
    }

    public void setButtons(List<ELSIoTButton> buttons) {
        this.buttons = buttons;
        associateButtons();
    }

    private void associateButtons() {
        for (ELSIoTButton btn : this.getButtons()) {
            btn.setContainer(this);
        }
    }
}
