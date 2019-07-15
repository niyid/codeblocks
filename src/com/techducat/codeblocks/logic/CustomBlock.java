/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class CustomBlock extends BaseBlock {
    private static final Logger LOGGER = Logger.getLogger(CustomBlock.class.getName());
    
    private BaseBlock root;
    
    private boolean shared;
    
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public Object call() throws Exception {
        root.setInputs(this.getInputs());
        root.setState(this.getState());

        Future[] fts = root.initiate();
        return this.sendOut(fts[0].get());
    }

    public void init() {
        //TODO Use block name and path to get saved definition; use definition to recreate block graph.
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }    
}
