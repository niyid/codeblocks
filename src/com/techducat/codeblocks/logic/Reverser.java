/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class Reverser extends BaseBlock {
        
    private static final Logger LOGGER = Logger.getLogger(Reverser.class.getName());

    public Reverser() {
        super("Reverser");
    }

    public Reverser(String name) {
        super(name);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[]{getName(), inputs});
        Set<Object> reverseOutput = new HashSet<>();
        if (inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((word) -> {
                LOGGER.log(Level.INFO, "word ==> {0}", word);
                reverseOutput.add(new StringBuilder(word.toString().toLowerCase()).reverse().toString());
                LOGGER.log(Level.INFO, "reverseOutput ==> {0}", reverseOutput);
                LOGGER.log(Level.INFO, " <= {0}", inputs);
            });
        }
                
        return this.sendOut(reverseOutput);
    }
}
