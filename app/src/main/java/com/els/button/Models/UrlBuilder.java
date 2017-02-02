package com.els.button.Models;

import android.util.Log;

/**
 * Created by Cam on 1/26/17.
 */

public class UrlBuilder {

    private String host;

    public UrlBuilder(String host) {
        this.host = host;
    }

    public String create(ELSLimri limriData, ELSLimriButtonPressAction action) {
        String url = null;
        Log.d("URLBuilder", "action given: " + action.toString());
        switch (action) {
            case LOADSHEET:
                url = getDisplayClientLocation() + "?id=" + limriData.getInventoryID() + "&pin=" + limriData.getPin() + "&sheet=" + limriData.getButton().getLocation() + "#";
                break;
            case LOADURL:
                // TODO: make loadurl it's own variable from content server?
                url = limriData.getButton().getLocation();
            case NOTHING:
                break;
        }
        return url;
    }


    private String getServerURL() {
        return "http://" + host + ":8080/ContentServer/ContentServer";
    }

    private String getDisplayClientLocation() {
        return "http://" + host + ":8091/DisplayClient/NewDisplay.html";
    }

}
