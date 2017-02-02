package com.els.button.Models;

import android.util.Log;

/**
 * Created by Cam on 1/24/17.
 */

public enum ELSLimriButtonPressAction {
    LOADSHEET("loadSheet"),
    LOADURL("loadURL"),
    NOTHING("nothing");

    private String literalAction;

    ELSLimriButtonPressAction(String literalAction) {
        this.literalAction = literalAction;
    }

    public String getLiteralAction() {
        return literalAction;
    }

    static public ELSLimriButtonPressAction fromStringLiteral(String stringLiteral) {
        Log.d("ELSLimriButtonPress...", "string literal given: " + stringLiteral);
        if (stringLiteral.equals("loadSheet")) {
            return LOADSHEET;
        } else if (stringLiteral.equals("loadURL")) {
            return LOADURL;
        } else {
            return NOTHING;
        }
    }
}
