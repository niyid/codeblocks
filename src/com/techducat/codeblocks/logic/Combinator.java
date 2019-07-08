/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.paukov.combinatorics3.Generator;

/**
 *
 * @author odada
 */
public class Combinator extends BaseBlock {
        
    private static final Logger LOGGER = Logger.getLogger(Combinator.class.getName());

    private int combinationSize;
    
    public Combinator() {
        super("1");
    }

    public Combinator(String name, int combinationSize) {
        super(name);
        this.combinationSize = combinationSize;
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[] {getName(), inputs});
        Stream<List<Object>> combinationOutput;
        List<List<Object>> combinationList = new ArrayList<>();
        if(inputs != null && !inputs.isEmpty()) {
            try {
                combinationSize = Integer.parseInt(getValue().toString());
                combinationOutput = Generator.combination(inputs).simple(combinationSize).stream();
                combinationOutput.forEach((c) -> {
                    LOGGER.log(Level.INFO, "combinations ==> {0}", c);
                    LOGGER.log(Level.INFO, " <= {0}", inputs);
                    combinationList.add(c);
                });
            } catch(NumberFormatException e) {
                
            }
        }
        
        return this.sendOut(combinationList);
    }
}
