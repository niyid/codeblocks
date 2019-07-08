/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import com.techducat.codeblocks.util.LexiconHelper;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class Lexicon extends BaseBlock {
    
    private static final Logger LOGGER = Logger.getLogger(Lexicon.class.getName());
    
    public static enum LexiconFunction {LOOKUP, DEFINE, SYNONYM, ANTHONYM};

    //TODO Use currently restricted to word validity check; later to include definition and synonyms.
    public Lexicon() {
        super("Lexicon");
    }

    public Lexicon(String name) {
        super(name);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[] {getName(), inputs});
        Set<Object> lexiconOutput = new HashSet<>();
        if(inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((word) -> {
                LOGGER.log(Level.INFO, "word ==> {0}", word);
                Thing t = new Thing(System.currentTimeMillis(), "", "Not found", null);
                Thing.setCategory(t, "Anything");
                lexiconOutput.add(LexiconHelper.lookup(word.toString()) != null ? word : t);
                LOGGER.log(Level.INFO, "lexiconOutput ==> {0}", lexiconOutput);
                LOGGER.log(Level.INFO, " <= {0}", inputs);
            });
        }
        
        return this.sendOut(lexiconOutput);
    }
}
