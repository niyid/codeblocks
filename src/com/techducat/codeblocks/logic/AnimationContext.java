/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author odada
 */
public class AnimationContext extends HashMap<String, Object> implements Comparable<AnimationContext> {

    public AnimationContext(int initialCapacity) {
        super(initialCapacity);
    }

    public AnimationContext() {
    }

    @Override
    public int compareTo(AnimationContext o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public BaseBlock getBlock() {
        return (BaseBlock) get("block");
    }
    
    public Object getIn() {
        return get("in");
    }
    
    public Object getOut() {
        return get("out");
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnimationContext other = (AnimationContext) obj;
        return Objects.equals(this.getBlock(), other.getBlock());
    }
}
