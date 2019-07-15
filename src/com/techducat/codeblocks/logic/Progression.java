/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

/**
 *
 * @author odada
 */
public class Progression extends BaseBlock {

    private static final Logger LOGGER = Logger.getLogger(Progression.class.getName());

    private static ExpressionEvaluator evaluator = new ExpressionEvaluator();
    
    public static enum TermType {SEQUENCE, SERIES};
    
    private TermType termType = TermType.SEQUENCE;

    public Progression() {
        super("n");
    }

    public Progression(String name) {
        super(name);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[]{getName(), inputs});
        Set<Object> termOutput = new HashSet<>();
        if (inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((termIndex) -> {
                LOGGER.log(Level.INFO, "termIndex ==> {0}", termIndex);
                try {
                    int index = Integer.parseInt(termIndex.toString());
                    termOutput.add(calculate(index, getValue().toString()));
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Input not an integer: {0}", termIndex);
                    LOGGER.log(Level.INFO, "Wrong format: {0}", e.getMessage());
                }
                LOGGER.log(Level.INFO, "termOutput ==> {0}", termOutput);
                LOGGER.log(Level.INFO, " <= {0}", inputs);
            });
        }

        return this.sendOut(new HashSet(termOutput));
    }

    public int calculate(int nmax, String function) {
        int term = 0;
        try {
            //function is f(n)

            String[] variableNames = {"n"};
            Class[] variableTypes = {Integer.class};
            evaluator.setParameters(variableNames, variableTypes);
            evaluator.setExpressionType(Integer.class);
            evaluator.cook(function);

            Integer[] variable;
            for (int i = 1; i <= nmax; ++i) {
                variable = new Integer[1];
                variable[0] = i;
                if(TermType.SEQUENCE.equals(termType)) {
                    term = (Integer) evaluator.evaluate(variable);
                } else {
                    term += (Integer) evaluator.evaluate(variable);
                }
            }

        } catch (CompileException | InvocationTargetException ex) {
            Logger.getLogger(Progression.class.getName()).log(Level.SEVERE, null, ex);
        }
        return term;
    }

    public TermType getTermType() {
        return termType;
    }

    public void setTermType(TermType termType) {
        this.termType = termType;
    }
}
