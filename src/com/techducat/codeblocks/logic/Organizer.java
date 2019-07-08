/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.techducat.codeblocks.logic;

import static com.techducat.codeblocks.logic.BaseBlock.createInstance;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class Organizer extends BaseBlock {
    
    public static enum SortDirection {ASCENDING, DESCENDING};
        
    private static final Logger LOGGER = Logger.getLogger(Organizer.class.getName());
    
    private Comparator comparator = new AscendingSorter();

    public Organizer() {
        super("Organizer");
    }

    public Organizer(String name) {
        super(name);
    }

    //Alternatively use enum value indices 0=ASCENDING, 1=DESCENDING
    public Organizer(String name, SortDirection direction) {
        super(name);
        switch(direction) {
            case ASCENDING:
            default:
                comparator = new AscendingSorter();
                break;
            case DESCENDING:
                comparator = new DescendingSorter();
                break;
        }
    }

    @Override
    public Object call() {
        LOGGER.log(Level.INFO, "{0} inputs => {1}", new Object[] {getName(), inputs});
    
        TreeSet<Object> sortedOutput = new TreeSet<>(comparator);
        if(inputs != null && !inputs.isEmpty()) {
            sortedOutput.addAll(inputs);
        }
        
        return sendOut(sortedOutput);
    }
    
    private class AscendingSorter implements Comparator<Thing> {

        @Override
        public int compare(Thing o1, Thing o2) {
            return o1 != null && o2 != null ? o1.getName().compareTo(o2.getName()) : -1;
        }        
    }
    
    private class DescendingSorter implements Comparator<Thing> {

        @Override
        public int compare(Thing o1, Thing o2) {
            return o1 != null && o2 != null ? o2.getName().compareTo(o1.getName()) : -1;
        }        
    }

    public static Organizer createInstance(Long id, Object direction, Collection<Object> inputs, int delay) throws InstantiationException, IllegalAccessException {
        Organizer.SortDirection dir = SortDirection.ASCENDING;
        if(direction instanceof SortDirection) {
            dir = (SortDirection) direction;
        } else if(direction instanceof String) {
            try {
                int sortIndex = Integer.parseInt(direction.toString());
                if(sortIndex == 0 || sortIndex == 1) {
                    dir = SortDirection.values()[sortIndex];
                }
            } catch(NumberFormatException e) {
                dir = SortDirection.valueOf(direction.toString());
            }
        }
        Organizer instance = (Organizer) createInstance(Organizer.class, id, dir, inputs, delay);
//        instance.setDelay(4);

        return instance;
    }    
}