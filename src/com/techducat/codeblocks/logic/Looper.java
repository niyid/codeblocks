/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import static com.techducat.codeblocks.logic.BaseBlock.SERVICE;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class Looper extends BaseBlock {
    
    private static final Logger LOGGER = Logger.getLogger(Looper.class.getName());

    public static enum LooperType {

        BAG, LIST
    }

    public Looper() {
        super("Looper");
    }

    public Looper(String name) {
        super(name);
    }

    public Looper(String name, Collection<Object> inputs) {
        super(name);
    }

    public static Looper createInstance(Long id, Looper.LooperType type, Collection<Object> inputs, int delay) throws InstantiationException, IllegalAccessException {
        Looper instance = (Looper) createInstance(Looper.class, id, type, inputs, delay);
//        instance.setDelay(4);

        return instance;
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "~~Looper~~ executing...");
        Object output = null;
        if (inputList != null && !inputList.isEmpty()) {
                if (getValue() == LooperType.BAG) {
                    Collections.shuffle(inputList);
                }
            
            output = inputList.remove(0);
            inputs.remove(output);

            LOGGER.log(Level.INFO, "output => {0}", output);
            LOGGER.log(Level.INFO, " <= {0}", inputs);
        }

        return this.sendOut(output);
    }
    
    @Override
    public Future[] initiate() throws InterruptedException, ExecutionException {
        Future[] futures = new Future[inputs.size()];
        for(int i = 0; i < inputs.size(); i++) {
            synchronized(this) {
                futures[i] = SERVICE.submit(this);
            }
        }
        
        return futures;
    }

    public List getInputList() {
        return inputList;
    }
    
    @Override
    public boolean isInDisplayed() {
        return true;
    }
}
