package com.els.button.Models;

import android.util.Log;

import com.els.button.R;

/**
 * Created by Cam on 4/11/17.
 */

public enum ELSLimriIcon {


    MAN(R.drawable.human_male),
    WOMAN(R.drawable.human_female),
    MAN_WOMAN(R.drawable.human_male_female),
    NONE(R.drawable.empty);

    private int literalIcon;

    ELSLimriIcon(int icon) {
        this.literalIcon = icon;
    }

    public int getLiteralIcon() {
        return this.literalIcon;
    }

    static public ELSLimriIcon fromStringLiteral(String stringLiteral) {
        ELSLimriIcon iconFromStringLiteral = null;


        if (stringLiteral.equals("man")) {              iconFromStringLiteral = ELSLimriIcon.MAN; }
        else if (stringLiteral.equals("woman")) {       iconFromStringLiteral = ELSLimriIcon.WOMAN; }
        else if (stringLiteral.equals("man-woman")) {   iconFromStringLiteral = ELSLimriIcon.MAN_WOMAN; }
        else {
            Log.d("ELSLimriIcon", "Did not find a mapped icon");
            iconFromStringLiteral = ELSLimriIcon.NONE;
        }

        Log.d("ELSLimriIcon", "literal: " + stringLiteral + ", gives resource: " + iconFromStringLiteral.getLiteralIcon());

        return iconFromStringLiteral;
    }
}
