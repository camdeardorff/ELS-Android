package com.els.button.Models;

import java.util.Map;

/**
 * Created by cameron on 4/5/16.
 */
public class ELSIoT extends ELSEntity {

    public String qID;
    public Map<String,String> valueAndTitle;

    public ELSIoT(String inventoryID, String pin, String qID, Map<String, String> valueAndTitle) {
        super(inventoryID,pin);
        this.qID = qID;
        this.valueAndTitle = valueAndTitle;
    }

    public ELSIoT(String inventoryID, String pin, String qID, String title, String description, Map<String, String> valueAndTitle) {
        super(inventoryID, pin, title, description);
        this.qID = qID;
        this.valueAndTitle = valueAndTitle;
    }
}
