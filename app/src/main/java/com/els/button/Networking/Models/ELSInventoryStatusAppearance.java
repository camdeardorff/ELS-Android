package com.els.button.Networking.Models;

import android.util.Log;

import com.els.button.Models.ELSLimriColor;

/**
 * Created by Cam on 1/24/17.
 */

public class ELSInventoryStatusAppearance {

    private String status;
    private String buttonText;
    private ELSLimriColor buttonColor;

    public ELSInventoryStatusAppearance(String status, String buttonText, ELSLimriColor buttonColor) {
        this.status = status;
        this.buttonText = buttonText;
        this.buttonColor = buttonColor;
        Log.d("ELSInventoryStatusAppe", "color: " + buttonColor.toString());
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
}
