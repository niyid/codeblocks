/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author odada
 */
public class DayCalc extends BaseBlock {
    
    private static final Logger LOGGER = Logger.getLogger(DayCalc.class.getName());
    
    public static final String[] DAYS_OF_THE_WEEK = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public static final Thing[] WEEK_DAYS = new Thing[DAYS_OF_THE_WEEK.length];
    
    static {
        ImageIcon weekdayIcon;
        for(int i = 0; i < DAYS_OF_THE_WEEK.length; i++) {
            WEEK_DAYS[i] = new Thing();
            weekdayIcon = new ImageIcon(DayCalc.class.getResource("/com/techducat/codeblocks/thing/icons/week_day_" + i + ".png"));            
            WEEK_DAYS[i].setIcon(weekdayIcon);
            WEEK_DAYS[i] = new Thing();
            WEEK_DAYS[i].setId((long) i);
            WEEK_DAYS[i].setName(DAYS_OF_THE_WEEK[i]);
            WEEK_DAYS[i].setDescription("Day " + i + " of the week");
            Thing.setCategory(WEEK_DAYS[i], "Week Day");
        }
    }
            
    public DayCalc() {
        super("0");
    }

    @Override
    public Object call() throws Exception {
        Calendar cal = Calendar.getInstance();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        int displacement = 0;
        if (getValue() != null) {
            try {
                displacement = Integer.parseInt(getValue().toString());
            } catch (NumberFormatException e) {

            }
        }
        Set outputs = new HashSet(inputs.size());
        if (inputs != null) {
            for(Object i : inputs) {
                LOGGER.log(Level.INFO, "input_date => {0}", i);
                cal.setTime(sdf.parse(i.toString()));
                cal.add(Calendar.DAY_OF_YEAR, displacement);
                LOGGER.log(Level.INFO, "day_index => {0}", (cal.get(Calendar.DAY_OF_WEEK) - 1));
                outputs.add(WEEK_DAYS[cal.get(Calendar.DAY_OF_WEEK) - 1]);
            }
        }
        
        LOGGER.log(Level.INFO, "outputs => {0}", outputs);

        return this.sendOut(outputs);
    }

}
