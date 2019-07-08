/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author odada
 */
public class AnagramRunner {
    
    public static void main(String[] args) {
        Set<Object> inputs = new HashSet<>();
        inputs.add("Retain");
        inputs.add("Solve");
        inputs.add("Fate");
        inputs.add("Temper");
        inputs.add("Devil");
        inputs.add("Sing");
        inputs.add("Fleet");
        Anagram anagram = new Anagram("AnagramCheck");
        
        anagram.setInputs(inputs);
        anagram.setState(new State());
        ExecutorService service = Executors.newFixedThreadPool(20);
        service.submit(anagram);
    }
    
}
