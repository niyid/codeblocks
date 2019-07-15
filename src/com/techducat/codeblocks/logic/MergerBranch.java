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
public class MergerBranch extends BaseBlock {
    
    private Merger merger;

    public MergerBranch(String name, Merger merger) {
        super(name);
        this.merger = merger;
        addOutputBlock(merger);
    }
 
    @Override
    synchronized public final void addOutputBlock(Block outputBlock) {
        if(getOutputBlocks() == null) {
            setOutputBlocks(new HashSet<>());
        }
        getOutputBlocks().add(outputBlock);
        if(outputBlock != null) {
            outputBlock.setInputBlock(this);
        }
    }

    public Merger getMerger() {
        return merger;
    }

    public void setMerger(Merger merger) {
        this.merger = merger;
    }
}
