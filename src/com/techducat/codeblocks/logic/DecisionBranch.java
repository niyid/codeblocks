/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import com.techducat.codeblocks.Block;
import java.util.HashSet;

/**
 *
 * @author odada
 */
public class DecisionBranch extends BaseBlock {
    
    private Decision decision;

    public DecisionBranch(String name, Decision decision) {
        super(name);
        this.decision = decision;
    }
 
    @Override
    synchronized public void addOutputBlock(Block outputBlock) {
        if(getOutputBlocks() == null) {
            setOutputBlocks(new HashSet<>());
        }
        getOutputBlocks().add(outputBlock);
        if(outputBlock != null) {
            outputBlock.setInputBlock(this.getDecision());
        }
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }
}
