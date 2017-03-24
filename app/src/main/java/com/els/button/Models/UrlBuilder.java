package com.els.button.Models;

import com.els.button.Networking.Models.ELSInventoryStatusAction;

/**
 * Created by Cam on 1/26/17.
 */

public class UrlBuilder {

    private String contentServerLocation;
    private String displayClientLocation;

    public UrlBuilder(String contentServerLocation, String displayClientLocation) {
        this.contentServerLocation = contentServerLocation;
        this.displayClientLocation = displayClientLocation;
    }

    public String create(ELSLimri limriData, ELSInventoryStatusAction action) {
        String url = null;
        switch (action.getType()) {
            case LOAD_SHEET:
                url = this.displayClientLocation + "?id=" + limriData.getInventoryID() + "&pin=" + limriData.getPin() + "&sheet=" + action.getLocation() + "#";
                break;
            case LOAD_URL:
                url = limriData.getButton().getLocation();
                break;
            case NOTHING:
                break;
        }
        return url;
    }
}
