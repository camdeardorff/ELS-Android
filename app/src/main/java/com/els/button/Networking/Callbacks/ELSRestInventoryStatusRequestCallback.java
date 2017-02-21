package com.els.button.Networking.Callbacks;

import com.els.button.Networking.Models.ELSInventoryStatus;

/**
 * Created by Cam on 2/14/17.
 */

public interface ELSRestInventoryStatusRequestCallback {
    void onSuccess(ELSInventoryStatus inventoryStatus);
    void onFailure();
}
