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
public class Basket extends BaseBlock {
    
    private static final Logger LOGGER = Logger.getLogger(Basket.class.getName());
    
    private final Set<Object> collationOutput = new HashSet();
    
    public static final Set<Basket> ALL_BASKETS = new HashSet<>();

    public Basket() {
        super("Basket");
        ALL_BASKETS.add(this);
    }

    public Basket(String name) {
        super(name);
        ALL_BASKETS.add(this);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[]{getName(), inputs});

        if (inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((item) -> {
                LOGGER.log(Level.INFO, "item ==> {0}", item);
                collationOutput.add(item);
                LOGGER.log(Level.INFO, "collationOutput ==> {0}", collationOutput);
                LOGGER.log(Level.INFO, " <= {0}", inputs);
            });
        }
        
        LOGGER.log(Level.INFO, "All collated ==> {0}", collationOutput);
        
        return this.sendOut(new HashSet(collationOutput));
    }
    
    public void reset() {
        collationOutput.clear();
    }
    
    public static void resetAll() {
        ALL_BASKETS.stream().forEach((b) -> {
            b.reset();
        });
    }
}
