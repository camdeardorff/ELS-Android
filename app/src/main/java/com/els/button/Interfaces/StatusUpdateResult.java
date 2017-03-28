package com.els.button.Interfaces;

/**
 * Created by Cam on 3/28/17.
 */

public interface StatusUpdateResult {
    void success();
    void failureFromCredentials();
    void failureFromConnectivity();
}
