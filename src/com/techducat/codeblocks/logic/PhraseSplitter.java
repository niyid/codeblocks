/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class PhraseSplitter extends BaseBlock {
            
    private static final Logger LOGGER = Logger.getLogger(PhraseSplitter.class.getName());

    public PhraseSplitter() {
        super("Splitter");
    }

    public PhraseSplitter(String name) {
        super(name);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[] {getName(), inputs});
        HashSet<Object> splitOutput = new HashSet<>();
        if(inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((phrase) -> {
                LOGGER.log(Level.INFO, "word ==> {0}", phrase);
                //Default use single space to split phrase
                StringTokenizer st = new StringTokenizer(phrase.toString(), getValue().toString());
                while(st.hasMoreTokens()) {
                    Thing t = new Thing(System.currentTimeMillis(), st.nextToken(), "A word in a phrase", null);
                    Thing.setCategory(t, "Anything");
                    splitOutput.add(t);
                }                
                LOGGER.log(Level.INFO, "splitOutput ==> {0}", splitOutput);
                LOGGER.log(Level.INFO, " <= {0}", inputs);
            });                
        }
        
        return this.sendOut(splitOutput);
    }
}
