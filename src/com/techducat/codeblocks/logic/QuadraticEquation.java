/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class QuadraticEquation extends BaseBlock {

    private static final Logger LOGGER = Logger.getLogger(QuadraticEquation.class.getName());

    public QuadraticEquation() {
        super("QuadraticEquation");
    }

    public QuadraticEquation(String name) {
        super(name);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[]{getName(), inputs});
        Set<Object> solutionOutput = new HashSet<>();
        if (inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((matrixRow) -> {
                if(matrixRow instanceof Thing) {
                    Thing thing = (Thing) matrixRow;
                    if(thing.getValue() instanceof double[]) {
                        double[] row = (double[]) thing.getValue();
                        LOGGER.log(Level.INFO, "matrixRow ==> {0}", matrixRow);
                        LOGGER.log(Level.INFO, "row ==> {0}", Arrays.asList(row));
                        try {
                            if(row.length == 3) {
                                solutionOutput.add(QuadraticEquation.calculate(row));
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Input not an integer: {0}", matrixRow);
                            LOGGER.log(Level.INFO, "Wrong format: {0}", e.getMessage());
                        }
                    }
                    LOGGER.log(Level.INFO, "solutionOutput ==> {0}", solutionOutput);
                    LOGGER.log(Level.INFO, " <= {0}", inputs);
                }
            });
        }

        return this.sendOut(new HashSet(solutionOutput)
        );
    }

    public static List<Thing> calculate(double[] coefficients) {
        List<Thing> solutions = new ArrayList<>(2);
        
        double a = coefficients[0];
        double b = coefficients[1];
        double c = coefficients[2];
        Thing t1 = new Thing();
        Thing t2 = new Thing();
        
        Double val1 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
        Double val2 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
        t1.setId(System.currentTimeMillis());
        t1.setName(val1.toString());
        t2.setId(System.currentTimeMillis());
        t2.setName(val2.toString());
        Thing.setCategory(t1, "Number");
        Thing.setCategory(t2, "Number");
        solutions.add(t1);
        solutions.add(t2);

        return solutions;
    }
    
    @Override
    public boolean isInDisplayed() {
        return true;
    }    
}
