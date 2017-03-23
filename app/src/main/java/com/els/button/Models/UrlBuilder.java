package com.els.button.Models;

import android.util.Log;

import com.els.button.Networking.Models.ELSInventoryStatusAction;

/**
 * Created by Cam on 1/26/17.
 */

public class UrlBuilder {

    private String host;

    public UrlBuilder(String host) {
        this.host = host;
    }

    public String create(ELSLimri limriData, ELSInventoryStatusAction action) {
        String url = null;
        Log.d("URLBuilder", "action display: " + limriData.getButton().isShouldShowActionResponse());
        switch (action.getType()) {
            case LOAD_SHEET:
                url = UrlBuilder.getDisplayClientLocation(host) + "?id=" + limriData.getInventoryID() + "&pin=" + limriData.getPin() + "&sheet=" + action.getLocation() + "#";
                break;
            case LOAD_URL:
                // TODO: make loadurl it's own variable from content server?
                url = limriData.getButton().getLocation();
            case NOTHING:
                break;
        }
        return url;
    }


    public static String getServerURL(String host) {
        return "http://" + host + ":8080/ContentServer/ContentServer";
    }

    public static String getDisplayClientLocation(String host) {
        return "http://" + host + ":8091/DisplayClient/NewDisplay.html";
    }

}
