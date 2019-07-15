/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class Fibonacci extends BaseBlock {

    private static final Logger LOGGER = Logger.getLogger(Fibonacci.class.getName());

    public Fibonacci() {
        super("Fibonacci");
    }

    public Fibonacci(String name) {
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
                    termOutput.add(Fibonacci.calculate(index));
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Input not an integer: {0}", termIndex);
                    LOGGER.log(Level.INFO, "Wrong format: {0}", e.getMessage());
                }
                LOGGER.log(Level.INFO, "termOutput ==> {0}", termOutput);
                LOGGER.log(Level.INFO, " <= {0}", inputs);
            });
        }

        return this.sendOut(new HashSet(termOutput)
        );
    }

    public static int calculate(int termIndex) {

        int term = 0;
        int t1 = 0, t2 = 1;
        for (int i = 1; i <= termIndex; ++i) {
            term = t1 + t2;
            t1 = t2;
            t2 = term;
        }

        return term;
    }
}
