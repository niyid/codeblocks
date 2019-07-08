/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author odada
 */
public class LooperRunner {
    
    public static void main(String[] args) {
        List<Object> inputs = new ArrayList<>();
        inputs.add("Retain");
        inputs.add("Solve");
        inputs.add("Fate");
        inputs.add("Temper");
        inputs.add("Devil");
        inputs.add("Sing");
        inputs.add("Fleet");
        Looper.LooperType looperType = Looper.LooperType.BAG;
        Looper instance = new Looper("Looper1");
        instance.setState(new State());
        instance.setValue(looperType);
        instance.setDelay(3);
        instance.setInputs(new HashSet<>());
        instance.getInputs().addAll(inputs);
        ExecutorService service = Executors.newFixedThreadPool(20);
        service.submit(instance);

    }
    
}
