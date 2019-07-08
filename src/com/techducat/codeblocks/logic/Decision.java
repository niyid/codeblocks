/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import com.mxgraph.model.mxCell;
import com.techducat.codeblocks.ui.CustomMxCell;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

/**
 *
 * @author odada
 */
public class Decision extends BaseBlock {

    private static final Logger LOGGER = Logger.getLogger(Decision.class.getName());

    private ExpressionEvaluator evaluator = new ExpressionEvaluator();

    private DecisionBranch branchYes;

    private DecisionBranch branchNo;
    
    private DecisionOperator operator = DecisionOperator.EQ;
    
    public static boolean flag;

    public Decision() {
        super("Decision");
        this.inputs = new HashSet<>();
    }

    public Decision(String name, String value) {
        super(name);
        setValue(value);

        branchYes = new DecisionBranch("Yes - " + this.getName(), this);
        branchNo = new DecisionBranch("No - " + this.getName(), this);
    }

    public Decision(String value, Set<Object> variables) {
        setValue(value);
        this.inputs = variables;

        branchYes = new DecisionBranch("Yes - " + this.getName(), this);
        branchNo = new DecisionBranch("No - " + this.getName(), this);
   }

    public Decision(String name, String value, Set<Object> variables) {
        super(name);
        setValue(value);
        this.inputs = variables;

        branchYes = new DecisionBranch("Yes - " + name, this);
        branchNo = new DecisionBranch("No - " + name, this);
    }

    public static Decision createInstance(Long id, Object value, Collection<Object> inputs, int delay) throws InstantiationException, IllegalAccessException {
        Decision instance = (Decision) createInstance(Decision.class, id, value, inputs, delay);

        if (instance.getBranchYes() == null) {
            DecisionBranch branchYes = new DecisionBranch("Yes", instance);
            branchYes.setInputBlock(instance);
            instance.setBranchYes(branchYes);
        }
        if (instance.getBranchNo() == null) {
            DecisionBranch branchNo = new DecisionBranch("No", instance);
            branchNo.setInputBlock(instance);
            instance.setBranchNo(branchNo);
        }
        if (instance.getOutputBlocks() == null) {
            instance.setOutputBlocks(new HashSet<>());
        }
        if (instance.getBranchYes().getOutputBlocks() == null) {
            instance.getBranchYes().setOutputBlocks(new HashSet<>());
        }
        if (instance.getBranchNo().getOutputBlocks() == null) {
            instance.getBranchNo().setOutputBlocks(new HashSet<>());
        }

        instance.setValue(value);
        if (instance.getInputs() == null) {
            instance.setInputs(new HashSet<>());
        }

        if (instance.getEvaluator() == null) {
            instance.setEvaluator(new ExpressionEvaluator());
        }

        return instance;
    }

    //TODO How to determine yes or no branch?
    public static void createLink(Decision source, BaseBlock target, CustomMxCell sourceCell, CustomMxCell targetCell, mxCell connectionEdge) {
        //Add the target to YES branch if first target to add
        //TODO ensure same instance is not added to both YES and NO branches.
        if (source.getBranchYes().getOutputBlocks().isEmpty()) {
            source.getBranchYes().addOutputBlock(target);
            connectionEdge.setValue("YES");
            target.setInputBlock(source);
            LOGGER.log(Level.INFO, "{0} added to {1}.YES", new Object[]{target, source});
        } else if (source.getBranchNo().getOutputBlocks().isEmpty() && !source.getBranchYes().getOutputBlocks().isEmpty() && !source.getBranchYes().getOutputBlocks().contains(target)) {
            source.getBranchNo().addOutputBlock(target);
            connectionEdge.setValue("NO");
            target.setInputBlock(source);
            LOGGER.log(Level.INFO, "{0} added to {1}.NO", new Object[]{target, source});
        } else {
            LOGGER.log(Level.WARNING, "YES => {0}; NO => {1}.", new Object[]{source.getBranchYes().getOutputBlocks(), source.getBranchNo().getOutputBlocks()});
        }
        sourceCell.setBlock(source);
        targetCell.setBlock(target);
    }

    protected String generateExpression() {
        //When operator is an IS, then use the category full qualified name for testing
        StringBuilder expr = new StringBuilder();
        StringBuilder[] subExprs = new StringBuilder[inputs.size()];

        Thing t;
        for (int i = 0; i < inputs.size(); i++) {
            subExprs[i] = new StringBuilder();
            if(i != 0) {
                subExprs[i].append(" && ");
            }
            
            t = (Thing) inputs.toArray()[i];
            subExprs[i].append(String.format(operator.getAction(), "x" + (i + 1)));

            LOGGER.log(Level.INFO, "Decision:Thing:Category => {0}", t.getCategory());
            expr.append(subExprs[i]);
        }
        LOGGER.log(Level.INFO, "Decision:Expression => {0}", expr.toString());
        
        return expr.toString();
    }

    @Override
    public Object call() {
        try {
            LOGGER.log(Level.INFO, "parameter => {0}", getValue());
            LOGGER.log(Level.INFO, "variables => {0}", inputs);
            List<Class> valueTypes = new ArrayList<>();
            //Parse input strings to check if numeric
            inputs.stream().forEach((v) -> {
                try {
                    Thing t = (Thing) v;
                    Double.parseDouble(t.getName());
                    valueTypes.add(Double.class);
                } catch(NumberFormatException e) {
                    if(DecisionOperator.IS.equals(operator)) {
                        valueTypes.add(String.class);
                    } else {
//                        valueTypes.add(v.getClass());
                        //TODO Use String for now
                        valueTypes.add(String.class);
                    }
                }
            });
            Class[] variableTypes = new Class[valueTypes.size() + 1];
            variableTypes = valueTypes.toArray(variableTypes);
            String[] variableNames = new String[inputs.size() + 1];

            for (int i = 0; i < inputs.size(); i++) {
                variableNames[i] = "x" + (i + 1);
            }

            //Parse input strings to check if numeric
            try {
                Double.parseDouble(getValue().toString());
                variableTypes[inputs.size()] = Double.class;
            } catch(NumberFormatException e) {
                if(DecisionOperator.IS.equals(operator)) {
                    variableTypes[inputs.size()] = String.class;
                } else {
                    variableTypes[inputs.size()] = getValue().getClass();
                }
            }
            variableNames[inputs.size()] = "val";
            evaluator.setParameters(variableNames, variableTypes);

            // And the value (i.e. "result") type is also "int".
            evaluator.setExpressionType(boolean.class);

            // And now we "cook" (scan, parse, compile and load) the fabulous value.
            evaluator.cook(generateExpression());

            // Eventually we evaluate the value - and that goes super-fast.
            Object[] forEvaluation = new Object[inputs.size() + 1];

            //Parse input strings to check if numeric
            Thing t;
            for(int i = 0; i < inputs.size(); i++) {
                t = (Thing) inputs.toArray()[i];
                if(variableTypes[i].isAssignableFrom(Double.class)) {
                    forEvaluation[i] = Double.parseDouble(t.getName());
                } else {
                    if(DecisionOperator.IS.equals(operator)) {
                        forEvaluation[i] = t.getCategory().getName();
                    } else {
                        forEvaluation[i] = t.getName();
                    }
                }
            }

            //TODO Ideally use getValue() to search by name/category for Thing/Category instance
            //TODO Remember to trim value
            forEvaluation[inputs.size()] = getValue();
            if(getValue().getClass().isAssignableFrom(Double.class)) {
                forEvaluation[inputs.size()] = Double.parseDouble(getValue().toString());
            } else {
                forEvaluation[inputs.size()] = getValue();
            }
            LOGGER.log(Level.INFO, "call -- types {0} vars {1} => vals {2}", new Object[]{Arrays.asList(variableTypes), Arrays.asList(variableNames), Arrays.asList(forEvaluation)});
            flag = (Boolean) evaluator.evaluate(forEvaluation);
            LOGGER.log(Level.INFO, "call -- {0} result => {1}", new Object[]{inputs, flag});

            if(this.getInputs() == null) {
                this.inputs = new HashSet<>();
            }
            if (flag) {
                this.setOutputBlocks(branchYes.getOutputBlocks());
            } else {
                this.setOutputBlocks(branchNo.getOutputBlocks());
            }
            
        } catch (CompileException | InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        Set out = new HashSet<>();
        out.addAll(inputs);
        return this.sendOut(out);
    }

    public DecisionBranch getBranchYes() {
        return branchYes;
    }

    public void setBranchYes(DecisionBranch branchYes) {
        this.branchYes = branchYes;
    }

    public DecisionBranch getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(DecisionBranch branchNo) {
        this.branchNo = branchNo;
    }

    public ExpressionEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(ExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public DecisionOperator getOperator() {
        return operator;
    }

    public void setOperator(DecisionOperator operator) {
        this.operator = operator;
    }
}
