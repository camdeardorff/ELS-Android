package com.els.button.Networking.Callbacks;

import org.w3c.dom.Document;

/**
 * Created by Cam on 2/14/17.
 */

public interface ELSRestRequestCallback {
    void onSuccess(Document document, Boolean result);
    void onFailure();
}

