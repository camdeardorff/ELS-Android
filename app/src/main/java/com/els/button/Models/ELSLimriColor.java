package com.els.button.Models;

import android.util.Log;

import com.els.button.R;

/**
 * Created by Cam on 1/26/17.
 */

public enum ELSLimriColor {

    BLACK(R.color.limri_black),
    WHITE(R.color.limri_white),
    DARK_BLUE(R.color.limri_dark_blue),
    LIGHT_BLUE(R.color.limri_light_blue),
    DARK_OLIVE(R.color.limri_dark_olive),
    LIGHT_OLIVE(R.color.limri_light_olive),
    TAN(R.color.limri_tan),
    RED(R.color.limri_red),
    YELLOW(R.color.limri_yellow),
    GREEN(R.color.limri_green);


    private int literalColor;

    ELSLimriColor(int color) {
        this.literalColor = color;
    }

    public int getLiteralColor() {
        return this.literalColor;
    }

    static public ELSLimriColor fromStringLiteral(String stringLiteral) {
        ELSLimriColor colorFromStringLiteral = null;

        if (stringLiteral.equals("black")) {            colorFromStringLiteral = ELSLimriColor.BLACK;}
        else if (stringLiteral.equals("white")) {       colorFromStringLiteral = ELSLimriColor.WHITE; }
        else if (stringLiteral.equals("darkBlue")) {    colorFromStringLiteral = ELSLimriColor.DARK_BLUE; }
        else if (stringLiteral.equals("lightBlue")) {   colorFromStringLiteral = ELSLimriColor.LIGHT_BLUE; }
        else if (stringLiteral.equals("darkOlive")) {   colorFromStringLiteral = ELSLimriColor.DARK_OLIVE; }
        else if (stringLiteral.equals("lightOlive")) {  colorFromStringLiteral = ELSLimriColor.LIGHT_OLIVE; }
        else if (stringLiteral.equals("tan")) {         colorFromStringLiteral = ELSLimriColor.TAN; }
        else if (stringLiteral.equals("red")) {         colorFromStringLiteral = ELSLimriColor.RED; }
        else if (stringLiteral.equals("yellow")) {      colorFromStringLiteral = ELSLimriColor.YELLOW; }
        else if (stringLiteral.equals("green")) {       colorFromStringLiteral = ELSLimriColor.GREEN; }
        else {
            Log.d("ELSLimriColor", "Did not find a real color, given: " + stringLiteral);
            colorFromStringLiteral = ELSLimriColor.BLACK;
        }

        return colorFromStringLiteral;
    }
}
