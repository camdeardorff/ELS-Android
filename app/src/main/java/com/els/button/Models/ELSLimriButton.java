package com.els.button.Models;

import com.els.button.Database.AppDatabase;
import com.els.button.Networking.Models.ELSInventoryStatusAction;
import com.els.button.Networking.Models.ELSInventoryStatusAppearance;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Cam on 1/26/17.
 */
//
@Table(database = AppDatabase.class)
public class ELSLimriButton extends BaseModel {

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    private String title;

    @Column
    private ELSLimriColor color;

    @Column
    private ELSLimriButtonPressAction action;

    @Column
    private String location;


    public ELSLimriButton() {
        this.title = "";
        this.color = ELSLimriColor.BLACK;
        this.action = ELSLimriButtonPressAction.NOTHING;
    }

    public ELSLimriButton(String title, ELSLimriColor color, ELSLimriButtonPressAction action, String location) {
        this.title = title;
        this.color = color;
        this.action = action;
        this.location = location;
    }

    public void updateAction(ELSInventoryStatusAction actionStatus) {
        if (actionStatus != null) {
            if (!actionStatus.getType().equals("")) {
                this.setAction(actionStatus.getType());
            }
            if (!actionStatus.getLocation().equals("")) {
                this.setLocation(actionStatus.getLocation());
            }
        }
    }

    public void updateAppearance(ELSInventoryStatusAppearance appearanceStatus) {
        if (appearanceStatus != null) {
            if (appearanceStatus.getButtonColor() != null) {
                this.setColor(appearanceStatus.getButtonColor());
            }
            if (!appearanceStatus.getButtonText().equals("")) {
                this.setTitle(appearanceStatus.getButtonText());
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ELSLimriButtonPressAction getAction() {
        return action;
    }

    public void setAction(ELSLimriButtonPressAction action) {
        this.action = action;
    }

    public ELSLimriColor getColor() {
        return color;
    }

    public void setColor(ELSLimriColor color) {
        this.color = color;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
