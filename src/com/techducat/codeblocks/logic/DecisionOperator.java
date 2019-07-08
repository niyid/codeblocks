/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

/**
 *
 * @author odada
 */
public enum DecisionOperator {

    EQ("%s.equals(val)"), GE("Double.compare(%s, val) >= 0"), LE("Double.compare(%s, val) <= 0"), IS("%s.equalsIgnoreCase(val.toString())");

    private final String action;

    public String getAction() {
        return this.action;
    }

    private DecisionOperator(String action) {
        this.action = action;
    }
}
