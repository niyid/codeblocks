/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import static com.techducat.codeblocks.logic.BaseBlock.createInstance;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class XterCounter extends BaseBlock {
        
    private static final Logger LOGGER = Logger.getLogger(XterCounter.class.getName());

    public XterCounter() {
        super("");
        this.setValue("");//Empty char
    }

    public XterCounter(String name, char target) {
        super(name);
        this.setValue(target);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[] {getName(), inputs});
        
        Set<Object> countOutput = new HashSet();
        if(inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((word) -> {
                LOGGER.log(Level.INFO, "word ==> {0}", word);
                Long count;
                if(getValue() == null || getValue().toString().isEmpty()) {
                    count = (long) word.toString().length();
                } else {
                    count = word.toString().toLowerCase().chars().filter(num -> num == Character.toLowerCase(getValue().toString().charAt(0))).count();
                }
                Thing t = new Thing(System.currentTimeMillis(), count.toString(), "Number " + count.toString(), null);
                Thing.setCategory(t, "Number");
                countOutput.add(t);
                
                LOGGER.log(Level.INFO, "countOutput ==> {0}", countOutput);
                LOGGER.log(Level.INFO, " <= {0}", inputs);
            });
        }
        
        return this.sendOut(countOutput);
    }
    
    public static XterCounter createInstance(Long id, Object ch, Collection<Object> inputs, int delay) throws InstantiationException, IllegalAccessException {
        XterCounter instance = (XterCounter) createInstance(XterCounter.class, id, ch, inputs, delay);
//        instance.setDelay(4);

        return instance;
    }
}
