/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author odada
 */
public class State {
    
    private BaseBlock block;
 
    private long executionCount = 2;
    
    public State() {
        executionCount = 2;
    }

    public BaseBlock getBlock() {
        return block;
    }

    public void setBlock(BaseBlock block) {
        this.block = block;
    }

    public void increment(BaseBlock block) {
        this.executionCount++;
        this.block = block;
    }
    
    public long getCount() {
        return executionCount;
    }

    @Override
    public String toString() {
        return "State{" + "block=" + block + ", executionCount=" + executionCount + '}';
    }
}
