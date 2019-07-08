/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import com.techducat.codeblocks.util.AnagramSolver;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class Anagram extends BaseBlock {
    
    private static final Logger LOGGER = Logger.getLogger(Anagram.class.getName());

    private AnagramSolver anagramSolver;

    private int loopIndex = 0;

    public Anagram() {
        super("Anagram");
    }

    public Anagram(String name) {
        super(name);
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[]{getName(), inputs});
        Set<Object> anagramOutput = new HashSet();
        if (inputs != null && !inputs.isEmpty()) {
            List<Integer> looperIndices = new ArrayList<>(inputs.size());
            for (int i = 0; i < inputs.size(); i++) {
                looperIndices.add(i);
            }
            looperIndices.stream().forEach((idx) -> {
                try {
                    String word = inputs.toArray()[0].toString();
                    anagramSolver = new AnagramSolver(word.length(), "/com/techducat/codeblocks/resources/words.txt");
                    LOGGER.log(Level.INFO, "word ==> {0}", word);
                    Set<Set<String>> anagrams = anagramSolver.findAllAnagrams(word.toLowerCase());
                    LOGGER.log(Level.INFO, "anagrams ==> {0}", anagrams);
                    anagrams.stream().forEach((x) -> {
                        x.stream().forEach((y) -> {
                            anagramOutput.add(y);
                        });
                    });
                    anagramOutput.remove(word.toLowerCase());
                    this.addExecution(anagramOutput);
                    this.sendOut(anagramOutput);
                    LOGGER.log(Level.INFO, "anagramOutput ==> {0}", anagramOutput);
                    loopIndex = idx;
                    LOGGER.log(Level.INFO, " <= {0}", inputs);
                } catch (IOException ex) {
                    Logger.getLogger(Anagram.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        return this.sendOut(anagramOutput);
    }

    public int getLoopIndex() {
        return loopIndex;
    }

    public void setLoopIndex(int loopIndex) {
        this.loopIndex = loopIndex;
    }
}
