package com.els.button.Models;

/**
 * Created by cameron on 3/31/16.
 */
public interface InventoryListAdapterDelegate {

    public void limriButtonWasPressed(ELSLimri elsLimri, ELSLimriButtonPressAction action);
    public void iotButtonWasPressed(ELSIoT elsIoT, String value);
}
