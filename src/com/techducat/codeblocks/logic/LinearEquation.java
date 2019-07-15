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
public class LinearEquation extends BaseBlock {

    private static final Logger LOGGER = Logger.getLogger(LinearEquation.class.getName());
            
    private final Set<double[]> equations = new HashSet();
    
    public static final Set<LinearEquation> ALL_LINEAR = new HashSet<>();

    public LinearEquation() {
        super("LinearEquation");
        ALL_LINEAR.add(this);
    }

    public LinearEquation(String name) {
        super(name);
        ALL_LINEAR.add(this);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[]{getName(), inputs});
        Set<Object> solutionOutput = new HashSet<>();
        if (inputs != null && !inputs.isEmpty()) {
            inputs.stream().forEach((matrixRow) -> {
                if (matrixRow instanceof Thing) {
                    Thing thing = (Thing) matrixRow;
                    LOGGER.log(Level.INFO, "row ==> {0}", Arrays.asList(thing.getValue()));
                    if (thing.getValue() instanceof double[]) {
                        double[] row = (double[]) thing.getValue();
                        LOGGER.log(Level.INFO, "matrixRow ==> {0}", matrixRow);
                        try {
                            if (row.length == 3) {
                                equations.add(row);
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Input not an integer: {0}", matrixRow);
                            LOGGER.log(Level.INFO, "Wrong format: {0}", e.getMessage());
                        }
                    }
                    LOGGER.log(Level.INFO, "equations ==> {0}", equations);
                    LOGGER.log(Level.INFO, " <= {0}", inputs);
                }
            });
            if (equations.size() == 2) {
                solutionOutput.add(calculate(equations));
                LOGGER.log(Level.INFO, "solutionOutput ==> {0}", solutionOutput);
            }
        }
        return this.sendOut(new HashSet(solutionOutput));

    }

    public static List<Thing> calculate(Set<double[]> coefficients) {
        List<Thing> solutions = new ArrayList<>(2);

        double[][] coeffs = new double[2][3];
        coefficients.toArray(coeffs);

        double a1 = coeffs[0][0];
        double b1 = coeffs[0][1];
        double c1 = coeffs[0][2];
        double a2 = coeffs[1][0];
        double b2 = coeffs[1][1];
        double c2 = coeffs[1][2];
        
        LOGGER.log(Level.INFO, "equation1 ==> {0}x + {1}y = {2}", new Object[]{a1, b1, c1});
        LOGGER.log(Level.INFO, "equation2 ==> {0}x + {1}y = {2}", new Object[]{a2, b2, c2});

        Double y = (c2 - a2 * c1 / a1) / (b2 - a2 * b1 / a1);
        Double x = (c1 - b1 * y) / a1;

        Thing t1 = new Thing();
        Thing t2 = new Thing();
        t1.setId(System.currentTimeMillis());
        t1.setName(x.toString());
        t2.setId(System.currentTimeMillis());
        t2.setName(y.toString());
        Thing.setCategory(t1, "Number");
        Thing.setCategory(t2, "Number");
        solutions.add(t1);
        solutions.add(t2);

        return solutions;
    }
    
    public void reset() {
        equations.clear();
    }
    
    public static void resetAll() {
        ALL_LINEAR.stream().forEach((b) -> {
            b.reset();
        });
    }
    
    @Override
    public boolean isInDisplayed() {
        return true;
    }    
}
