/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import static com.techducat.codeblocks.logic.BaseBlock.createInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.paukov.combinatorics3.Generator;
import org.paukov.combinatorics3.PermutationGenerator;

/**
 *
 * @author odada
 */
public class Permutator extends BaseBlock {
        
    private static final Logger LOGGER = Logger.getLogger(Permutator.class.getName());

    private int permutationSize;
    
    public Permutator() {
        super("1");
    }

    public Permutator(String name, int permutationSize) {
        super(name);
        this.permutationSize = permutationSize;
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[] {getName(), inputs});
        Stream<List<Object>> permutationOutput;
        List<List<Object>> permutationList = new ArrayList<>();
        if(inputs != null && !inputs.isEmpty()) {
            try {
                permutationSize = Integer.parseInt(getValue().toString());
                permutationOutput = Generator.permutation(inputs).simple(PermutationGenerator.TreatDuplicatesAs.DIFFERENT).stream();
                permutationOutput.forEach((c) -> {
                    LOGGER.log(Level.INFO, "permutations ==> {0}", c);
                    LOGGER.log(Level.INFO, " <= {0}", inputs);
                    permutationList.add(c);
                });
            } catch(NumberFormatException e) {
                
            }
        }
        
        return this.sendOut(permutationList);
    }

    public static Permutator createInstance(Long id, Object treatDuplicate, Collection<Object> inputs, int delay) throws InstantiationException, IllegalAccessException {
        PermutationGenerator.TreatDuplicatesAs dir = PermutationGenerator.TreatDuplicatesAs.DIFFERENT;
        if(treatDuplicate instanceof PermutationGenerator.TreatDuplicatesAs) {
            dir = (PermutationGenerator.TreatDuplicatesAs) treatDuplicate;
        } else if(treatDuplicate instanceof String) {
            try {
                int sortIndex = Integer.parseInt(treatDuplicate.toString());
                if(sortIndex == 0 || sortIndex == 1) {
                    dir = PermutationGenerator.TreatDuplicatesAs.values()[sortIndex];
                }
            } catch(NumberFormatException e) {
                dir = PermutationGenerator.TreatDuplicatesAs.valueOf(treatDuplicate.toString());
            }
        }
        Permutator instance = (Permutator) createInstance(Permutator.class, id, dir, inputs, delay);
//        instance.setDelay(4);

        return instance;
    }        
}
