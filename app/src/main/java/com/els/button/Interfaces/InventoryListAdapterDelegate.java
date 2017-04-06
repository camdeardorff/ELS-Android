package com.els.button.Interfaces;

import com.els.button.Models.ELSIoT;
import com.els.button.Models.ELSLimri;
import com.els.button.Models.ELSLimriButtonPressAction;

/**
 * Created by cameron on 3/31/16.
 */
public interface InventoryListAdapterDelegate {

    public void limriButtonWasPressed(ELSLimri elsLimri, ELSLimriButtonPressAction action);
    public void limriConfigurationButtonWasPressed(ELSLimri elsLimri);
    public void iotButtonWasPressed(ELSIoT elsIoT, String value);
}
