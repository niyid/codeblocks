package com.techducat.codeblocks;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.techducat.codeblocks.logic.State;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author odada
 */
public interface Block extends Callable<Object> {
    
    /**
     * 
     * @param delay 
     */
    void setDelay(int delay);
    
    /**
     * 
     * @return 
     */
    int getDelay();
    
    /**
     * 
     * @param inputs 
     */
    void sendIn(Set<Object> inputs); 

    /**
     * 
     * @param output 
     * @return  
     */
    Object sendOut(Object output);
    
    /**
     * 
     * @return 
     */
    Set<Block> getOutputBlocks();
    
    /**
     * 
     * @param outputBlocks 
     */
    void setOutputBlocks(Set<Block> outputBlocks);
    
    /**
     * 
     * @return 
     */
    Block getInputBlock();
    
    /**
     * 
     * @param inputBlock 
     */
    void setInputBlock(Block inputBlock);
    
    /**
     * 
     * @param outputBlock 
     */
    void addOutputBlock(Block outputBlock);
    
    /**
     * 
     * @return 
     */
    String getName();
    
    /**
     * 
     * @param name 
     */
    void setName(String name);
    
    /**
     * 
     * @return 
     */
    Block findRoot();
    
    /**
     * 
     * @param value 
     */
    void setValue(Object value);
    
    /**
     * 
     * @return 
     */
    Object getValue();
    
    /**
     * 
     * @param id 
     */
    void setId(Long id);
    
    /**
     * 
     * @return 
     */
    Long getId();
    
    /**
     * 
     * @return 
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    Future[] initiate() throws InterruptedException, ExecutionException;
    
    /**
     * 
     * @return 
     */
    Future getFuture();
    
    /**
     * 
     * @return 
     */
    State getState();
    
    /**
     * 
     * @param state 
     */
    void setState(State state);
    
    /**
     * 
     * @return 
     */    
    Set<Object> getInputs();
    
    /**
     * 
     * @param inputs 
     */
    void setInputs(Set<Object> inputs);
    
    /**
     * 
     */
    void readInput();
    
    /**
     * 
     * @return 
     */
    Collection<Object> getRetained();
    
    /**
     * 
     */
    void initInput();
    
    /**
     * 
     * @return 
     */
    boolean isInDisplayed();
}
