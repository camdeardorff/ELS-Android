package com.els.button.Models;

/**
 * Created by Cam on 1/24/17.
 */

public enum ELSLimriButtonPressAction {
    SET_QUESTION("set-question"),
    INC_QUESTION("inc-question"),
    LOAD_SHEET("load-sheet"),
    LOAD_URL("load-url"),
    REFRESH("refresh"),
    NOTHING("nothing");

    private String literalAction;

    ELSLimriButtonPressAction(String literalAction) {
        this.literalAction = literalAction;
    }

    public String getLiteralAction() {
        return literalAction;
    }

    static public ELSLimriButtonPressAction fromStringLiteral(String stringLiteral) {

       if (SET_QUESTION.getLiteralAction().equals(stringLiteral)) {         return SET_QUESTION; }
       else if (INC_QUESTION.getLiteralAction().equals(stringLiteral)) {    return INC_QUESTION; }
       else if (LOAD_SHEET.getLiteralAction().equals(stringLiteral)) {      return LOAD_SHEET; }
       else if (LOAD_URL.getLiteralAction().equals(stringLiteral)) {        return LOAD_URL; }
       else if (REFRESH.getLiteralAction().equals(stringLiteral)) {         return REFRESH; }
       else {                   /* nothing */                               return NOTHING; }
    }
}
