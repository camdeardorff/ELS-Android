package com.els.button.Networking.Models;

import com.els.button.Models.ELSLimriColor;

/**
 * Created by Cam on 1/24/17.
 */

public class ELSInventoryStatusAppearance {

    private String status;
    private String buttonText;
    private ELSLimriColor buttonColor;
    private ELSLimriColor buttonTextColor;
    private ELSLimriColor buttonBorderColor;
    private Integer buttonBorderWidth;
    private Integer buttonBorderRadius;


    public ELSInventoryStatusAppearance(String status, String buttonText, ELSLimriColor buttonTextColor, ELSLimriColor buttonColor, ELSLimriColor buttonBorderColor, Integer buttonBorderWidth, Integer buttonBorderRadius) {
        this.status = status;
        this.buttonText = buttonText;
        this.buttonTextColor = buttonTextColor;
        this.buttonColor = buttonColor;
        this.buttonBorderColor = buttonBorderColor;
        this.buttonBorderWidth = buttonBorderWidth;
        this.buttonBorderRadius = buttonBorderRadius;
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

    public ELSLimriColor getButtonBorderColor() {
        return buttonBorderColor;
    }

    public void setButtonBorderColor(ELSLimriColor buttonBorderColor) {
        this.buttonBorderColor = buttonBorderColor;
    }

    public Integer getButtonBorderWidth() {
        return buttonBorderWidth;
    }

    public void setButtonBorderWidth(Integer buttonBorderWidth) {
        this.buttonBorderWidth = buttonBorderWidth;
    }

    public Integer getButtonBorderRadius() {
        return buttonBorderRadius;
    }

    public void setButtonBorderRadius(Integer buttonBorderRadius) {
        this.buttonBorderRadius = buttonBorderRadius;
    }

    public ELSLimriColor getButtonTextColor() {
        return buttonTextColor;
    }

    public void setButtonTextColor(ELSLimriColor buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }
}
