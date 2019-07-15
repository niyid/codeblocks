/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import com.mxgraph.model.mxCell;
import com.techducat.codeblocks.ui.CustomMxCell;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class Merger extends BaseBlock {

    private static final Logger LOGGER = Logger.getLogger(Merger.class.getName());

    private MergerBranch branchYes;

    private MergerBranch branchNo;

    public Merger() {
        super("Merger");
        this.inputs = new HashSet<>();
    }

    public Merger(String name, String value) {
        super(name);
        setValue(value);

        branchYes = new MergerBranch("Yes - " + this.getName(), this);
        branchNo = new MergerBranch("No - " + this.getName(), this);
    }

    public Merger(String value, Set<Object> variables) {
        setValue(value);
        this.inputs = variables;

        branchYes = new MergerBranch("Yes - " + this.getName(), this);
        branchNo = new MergerBranch("No - " + this.getName(), this);
   }

    public Merger(String name, String value, Set<Object> variables) {
        super(name);
        setValue(value);
        this.inputs = variables;

        branchYes = new MergerBranch("Yes - " + name, this);
        branchNo = new MergerBranch("No - " + name, this);
    }

    public static Merger createInstance(Long id, Object value, Collection<Object> inputs, int delay) throws InstantiationException, IllegalAccessException {
        Merger instance = (Merger) createInstance(Merger.class, id, value, inputs, delay);

        if (instance.getBranchYes() == null) {
            MergerBranch branchYes = new MergerBranch("Yes", instance);
            branchYes.addOutputBlock(instance);
        }
        if (instance.getBranchNo() == null) {
            MergerBranch branchNo = new MergerBranch("No", instance);
            branchNo.addOutputBlock(instance);
        }
        if (instance.getOutputBlocks() == null) {
            instance.setOutputBlocks(new HashSet<>());
        }

        instance.setValue(value);
        if (instance.getInputs() == null) {
            instance.setInputs(new HashSet<>());
        }

        return instance;
    }

    //TODO How to determine yes or no branch?
    public static void createLink(Merger target, BaseBlock source, CustomMxCell sourceCell, CustomMxCell targetCell, mxCell connectionEdge) {
        //Add the target to YES branch if first target to add
        //TODO ensure same instance is not added to both YES and NO branches.
        if (target.getBranchYes().getInputBlock() == null) {
            target.getBranchYes().setInputBlock(target);
            connectionEdge.setValue("YES");
            source.addOutputBlock(target);
            LOGGER.log(Level.INFO, "{0} added to {1}.YES", new Object[]{target, source});
        } else if (target.getBranchNo().getInputBlock() == null && target.getBranchYes().getInputBlock() != null && target.getBranchYes().getInputBlock().equals(source)) {
            target.getBranchNo().setInputBlock(target);
            connectionEdge.setValue("NO");
            source.addOutputBlock(target);
            LOGGER.log(Level.INFO, "{0} added to {1}.NO", new Object[]{target, source});
        } else {
            LOGGER.log(Level.WARNING, "YES => {0}; NO => {1}.", new Object[]{target.getBranchYes().getOutputBlocks(), target.getBranchNo().getOutputBlocks()});
        }
        sourceCell.setBlock(source);
        targetCell.setBlock(target);
    }

    @Override
    public Object call() throws Exception {
                Set out = new HashSet<>();

        if(Decision.flag) {
            out.addAll(branchYes.getInputs());
        } else {
            out.addAll(branchNo.getInputs());
        }
        return this.sendOut(out);
    }

    public MergerBranch getBranchYes() {
        return branchYes;
    }

    public void setBranchYes(MergerBranch branchYes) {
        this.branchYes = branchYes;
    }

    public MergerBranch getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(MergerBranch branchNo) {
        this.branchNo = branchNo;
    }
}
