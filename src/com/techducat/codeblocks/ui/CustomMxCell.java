/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.ui;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.techducat.codeblocks.logic.BaseBlock;
import com.techducat.codeblocks.logic.Decision;
import com.techducat.codeblocks.logic.DecisionOperator;
import com.techducat.codeblocks.logic.Looper;
import com.techducat.codeblocks.logic.Organizer;
import com.techducat.codeblocks.logic.Permutator;
import com.techducat.codeblocks.logic.Progression;
import com.techducat.codeblocks.logic.XterCounter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public final class CustomMxCell extends mxCell {
    
    private static final Logger LOGGER = Logger.getLogger(AnimationRunner.class.getName());

    public CustomMxCell() {
    }

    public CustomMxCell(String name, Object value, mxGeometry geometry, String style) {
        super(value, geometry, style);
        setName(name);
    }

    private String name;

    private BaseBlock block;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (block != null) {
            block.setName(name);
        }
    }

    public BaseBlock getBlock() {
        return block;
    }

    public void setBlock(BaseBlock block) {
        this.block = block;
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        if (block != null) {
            block.setValue(value);
        }
    }

    //TODO On update value, update corresponding CodeBlock
    @Override
    public Object clone() throws CloneNotSupportedException {
        mxCell cell = (mxCell) super.clone();
        CustomMxCell clone = null;
        try {
            clone = CustomMxCell.class.newInstance();

            clone.setGeometry(cell.getGeometry());
            clone.setValue(cloneValue());
            clone.setStyle(getStyle());
            clone.setCollapsed(isCollapsed());
            clone.setConnectable(isConnectable());
            clone.setEdge(isEdge());
            clone.setVertex(isVertex());
            clone.setVisible(isVisible());
            clone.setParent(null);
            clone.setSource(null);
            clone.setTarget(null);
            clone.children = null;
            clone.edges = null;
            clone.setName(getName());

            mxGeometry geom = getGeometry();

            if (geom != null) {
                clone.setGeometry((mxGeometry) geom.clone());
            }

        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(CustomMxCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clone;
    }

    public void mxCell2BaseBlock(String templateName, mxCell cell, Class<? extends BaseBlock> clazz) throws InstantiationException, IllegalAccessException {
        LOGGER.log(Level.INFO, "mxCell2BaseBlock --{0} {1}", new Object[]{templateName, clazz});
        BaseBlock baseBlock = null;
        if (baseBlock == null) {
            if (null != clazz.getName()) {
                switch (clazz.getName()) {
                    case "com.techducat.codeblocks.logic.Looper":
                        baseBlock = Looper.createInstance(Long.valueOf(cell.getId()), (Looper.LooperType) cell.getValue(), new ArrayList<>(), 3);
                        break;
                    case "com.techducat.codeblocks.logic.Decision":
                        baseBlock = Decision.createInstance(Long.valueOf(cell.getId()), (String) cell.getValue(), new ArrayList<>(), 3);
                        Decision d = (Decision) baseBlock;
                        switch (templateName) {
                            case "Equal":
                            default:
                                d.setOperator(DecisionOperator.EQ);
                                break;
                            case "Is Type":
                                d.setOperator(DecisionOperator.IS);
                                break;
                            case "Less":
                                d.setOperator(DecisionOperator.LE);
                                break;
                            case "Greater":
                                d.setOperator(DecisionOperator.GE);
                                break;
                        }
                        break;
                    case "com.techducat.codeblocks.logic.Organizer":
                        baseBlock = Organizer.createInstance(Long.valueOf(cell.getId()), cell.getValue(), new ArrayList<>(), 3);
                        break;
                    case "com.techducat.codeblocks.logic.Permutator":
                        baseBlock = Permutator.createInstance(Long.valueOf(cell.getId()), cell.getValue(), new ArrayList<>(), 3);
                        break;
                    case "com.techducat.codeblocks.logic.XterCounter":
                        baseBlock = XterCounter.createInstance(Long.valueOf(cell.getId()), cell.getValue(), new ArrayList<>(), 3);
                        break;
                    case "com.techducat.codeblocks.logic.Progression":
                        baseBlock = BaseBlock.createInstance(clazz, Long.valueOf(cell.getId()), (String) cell.getValue(), new ArrayList<>(), 3);
                        Progression p = (Progression) baseBlock;
                        switch (templateName) {
                            case "Sequence":
                            default:
                                p.setTermType(Progression.TermType.SEQUENCE);
                                break;
                            case "Series":
                                p.setTermType(Progression.TermType.SERIES);
                                break;
                        }
                        break;
                    default:
                        baseBlock = BaseBlock.createInstance(clazz, Long.valueOf(cell.getId()), (String) cell.getValue(), new ArrayList<>(), 3);
                        break;
                }
            }
        }
        
        block = baseBlock;
    }

}
