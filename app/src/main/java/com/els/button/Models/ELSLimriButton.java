package com.els.button.Models;

import com.els.button.Database.AppDatabase;
import com.els.button.Networking.Models.ELSInventoryStatusAction;
import com.els.button.Networking.Models.ELSInventoryStatusAppearance;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;

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
    private ELSLimriColor textColor;

    @Column
    private ELSLimriButtonPressAction action;

    @Column
    private String location;

    @Column
    private Boolean shouldShowActionResponse;

    @Column
    private Integer cornerRadius;

    private ArrayList<ELSInventoryStatusAction> actions;

    public ELSLimriButton() {
        this.title = "";
        this.color = ELSLimriColor.BLACK;
        this.action = ELSLimriButtonPressAction.NOTHING;
    }

    public ELSLimriButton(String title, ELSLimriColor color, ELSLimriColor textColor, ELSLimriButtonPressAction action, String location, Boolean shouldShowActionResponse, Integer borderRadius) {
        this.title = title;
        this.color = color;
        this.textColor = textColor;
        this.action = action;
        this.location = location;
        this.shouldShowActionResponse = shouldShowActionResponse;
        this.cornerRadius = borderRadius;
        this.actions = new ArrayList<ELSInventoryStatusAction>();
    }

    public void updateAction(ELSInventoryStatusAction actionStatus) {
        if (actionStatus != null) {
            if (!actionStatus.getType().equals("")) {
                this.setAction(actionStatus.getType());
            }
            if (!actionStatus.getLocation().equals("")) {
                this.setLocation(actionStatus.getLocation());
            }
            this.setShouldShowActionResponse(actionStatus.getDisplay());
        }
        this.save();
    }


    public void updateAppearance(ELSInventoryStatusAppearance appearanceStatus) {
        if (appearanceStatus != null) {
            if (appearanceStatus.getButtonColor() != null) {
                this.setColor(appearanceStatus.getButtonColor());
            }
            if (appearanceStatus.getButtonTextColor() != null) {
                this.setTextColor(appearanceStatus.getButtonTextColor());
            }
            if (!appearanceStatus.getButtonText().equals("")) {
                this.setTitle(appearanceStatus.getButtonText());
            }
            if (appearanceStatus.getButtonCornerRadius() > -1) {
                this.setCornerRadius(appearanceStatus.getButtonCornerRadius());
            }
        }
        this.save();
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

    public Integer getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(Integer cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public ELSLimriColor getTextColor() {
        return textColor;
    }

    public void setTextColor(ELSLimriColor textColor) {
        this.textColor = textColor;
    }

    public Boolean isShouldShowActionResponse() {
        return shouldShowActionResponse;
    }

    public void setShouldShowActionResponse(Boolean shouldShowActionResponse) {
        this.shouldShowActionResponse = shouldShowActionResponse;
    }

    public ArrayList<ELSInventoryStatusAction> getActions() {
        return actions;
    }

    public void setActions(ArrayList<ELSInventoryStatusAction> actions) {
        this.actions = actions;
    }
}
