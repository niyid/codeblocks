/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.codehaus.commons.compiler.CompileException;

/**
 *
 * @author odada
 */
public class DecisionRunner {

    public static void main(String[] args) throws CompileException, InvocationTargetException {
        String expression = "x1.length() > 5";

        Set<Object> variables = new HashSet<>();
        variables.add("Shokolokobangoshe");

        Decision decision = new Decision("LongerThan5?", expression, variables);
        decision.setState(new State());
        ExecutorService service = Executors.newFixedThreadPool(20);
        service.submit(decision);
    }
}
