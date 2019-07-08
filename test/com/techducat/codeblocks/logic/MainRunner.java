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
public class MainRunner {
    
    public static void main(String[] args) {
        new MainRunner().execute();
    }
    
    public void execute() {
        State state = new State();
        
        List<Object> inputs = new ArrayList<>();
        inputs.add("Retain");
        inputs.add("Solve");
        inputs.add("Fate");
        inputs.add("Temper");
        inputs.add("Devil");
        inputs.add("Sing");
        inputs.add("Fleet");
        Looper.LooperType looperType = Looper.LooperType.BAG;
        Looper looper = new Looper("WordListLoop");
        looper.setValue(looperType);
        looper.setDelay(3);
        looper.setInputs(new HashSet<>());
        looper.getInputs().addAll(inputs);
        looper.setState(state);

        String expression = "x1.length() >= 5";

        Decision decision = new Decision("LenLongerEqual5", expression);
        
        looper.addOutputBlock(decision);
        
        Anagram anagram = new Anagram("AnagramLongerEqual5");
        
        decision.getBranchYes().addOutputBlock(anagram);
                    
        System.out.println("ROOT=" + looper.findRoot().getName());                    
        System.out.println("ROOT=" + decision.findRoot().getName());                    
        System.out.println("ROOT=" + anagram.findRoot().getName());
        
        //TODO Check for orphaned blocks before running.
        
        ExecutorService service = Executors.newFixedThreadPool(20);
        service.submit(looper);
        
    }
}
