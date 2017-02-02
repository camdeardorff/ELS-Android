package com.els.button.Models;

/**
 * Created by cameron on 3/31/16.
 */
public interface InventoryListAdapterDelegate {

    public void limriButtonWasPressedWithLimriInfo(ELSLimri elsLimri, ELSLimriButtonPressAction action);
    public void iotButtonWasPressedWithIotInfoAndSetQuestionValue(ELSIoT elsIoT, String value);
}
