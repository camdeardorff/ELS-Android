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
    private ELSLimriColor textColor;

    @Column
    private ELSLimriButtonPressAction action;

    @Column
    private String location;

    @Column
    private ELSLimriColor borderColor;

    @Column
    private Integer borderWidth;

    @Column
    private Integer borderRadius;

    public ELSLimriButton() {
        this.title = "";
        this.color = ELSLimriColor.BLACK;
        this.action = ELSLimriButtonPressAction.NOTHING;
    }

    public ELSLimriButton(String title, ELSLimriColor color, ELSLimriColor textColor, ELSLimriButtonPressAction action, String location, ELSLimriColor borderColor, Integer borderWidth, Integer borderRadius) {
        this.title = title;
        this.color = color;
        this.textColor = textColor;
        this.action = action;
        this.location = location;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.borderRadius = borderRadius;
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
            if (appearanceStatus.getButtonBorderColor() != null) {
                this.setBorderColor(appearanceStatus.getButtonColor());
            }
            if (!appearanceStatus.getButtonText().equals("")) {
                this.setTitle(appearanceStatus.getButtonText());
            }
            if (appearanceStatus.getButtonBorderWidth() > -1) {
                this.setBorderWidth(appearanceStatus.getButtonBorderWidth());
            }
            if (appearanceStatus.getButtonBorderRadius() > -1) {
                this.setBorderRadius(appearanceStatus.getButtonBorderRadius());
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

    public ELSLimriColor getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(ELSLimriColor borderColor) {
        this.borderColor = borderColor;
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Integer getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(Integer borderRadius) {
        this.borderRadius = borderRadius;
    }

    public ELSLimriColor getTextColor() {
        return textColor;
    }

    public void setTextColor(ELSLimriColor textColor) {
        this.textColor = textColor;
    }
}
