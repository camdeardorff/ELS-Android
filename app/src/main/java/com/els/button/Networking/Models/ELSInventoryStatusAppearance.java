package com.els.button.Networking.Models;

import com.els.button.Models.ELSLimriColor;
import com.els.button.Models.ELSLimriIcon;

/**
 * Created by Cam on 1/24/17.
 */

public class ELSInventoryStatusAppearance {

    private String status;
    private String buttonText;
    private ELSLimriColor buttonColor;
    private ELSLimriColor buttonTextColor;
    private Integer buttonCornerRadius;
    private ELSLimriIcon icon;

    public ELSInventoryStatusAppearance(String status, String buttonText, ELSLimriColor buttonTextColor, ELSLimriColor buttonColor, Integer buttonBorderRadius, ELSLimriIcon icon) {
        this.status = status;
        this.buttonText = buttonText;
        this.buttonTextColor = buttonTextColor;
        this.buttonColor = buttonColor;
        this.buttonCornerRadius = buttonBorderRadius;
        this.icon = icon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public ELSLimriColor getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(ELSLimriColor buttonColor) {
        this.buttonColor = buttonColor;
    }

    public Integer getButtonCornerRadius() {
        return buttonCornerRadius;
    }

    public void setButtonCornerRadius(Integer buttonCornerRadius) {
        this.buttonCornerRadius = buttonCornerRadius;
    }

    public ELSLimriColor getButtonTextColor() {
        return buttonTextColor;
    }

    public void setButtonTextColor(ELSLimriColor buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public ELSLimriIcon getIcon() {
        return icon;
    }

    public void setIcon(ELSLimriIcon icon) {
        this.icon = icon;
    }
}
