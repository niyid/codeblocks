/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class WordCounter extends BaseBlock {
    
    private static final Logger LOGGER = Logger.getLogger(WordCounter.class.getName());

    public WordCounter() {
        super("");
        this.setValue(""); //Empty string
    }

    public WordCounter(String name) {
        super(name);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[] {getName(), inputs});
                    
        Set<Object> countOutput = new HashSet();

        if(inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((word) -> {
                LOGGER.log(Level.INFO, "word ==> {0}", word);
                StringTokenizer tk =  new StringTokenizer(word.toString().toLowerCase(), getValue().toString().toLowerCase());
                Integer count = tk.countTokens();
                Thing t = new Thing(System.currentTimeMillis(), count.toString(), "Number " + count.toString(), null);
                Thing.setCategory(t, "Number");
                countOutput.add(t);
                LOGGER.log(Level.INFO, "countOutput ==> {0}", countOutput);
                LOGGER.log(Level.INFO, " <= {0}", inputs);
            });
        }
                
        return this.sendOut(countOutput);
    }
}
