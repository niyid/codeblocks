/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.logic;

import java.io.Serializable;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 *
 * @author odada
 */
public class Thing implements Serializable {
    
    public static String componentName;

    private Long id;

    private ImageIcon icon;

    private String name;

    private String description;

    private ThingCategory category;

    private double x;

    private double y;

    private double width;

    private double height;

    public int countLetters;
    
    public Thing() {
        
    }

    public Thing(Long id, String name, String description, ThingCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (name != null) {
            countLetters = name.length();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ThingCategory getCategory() {
        return category;
    }

    public void setCategory(ThingCategory category) {
        this.category = category;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Thing other = (Thing) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.category, other.category);
    }
    
    public static void setCategory(Thing thing, String name) {
        ThingCategory c1 = new ThingCategory();
        c1.setName("");
        c1.setDescription("");
        c1.setId(0L);
        if(name != null) {
            switch(name) {
                case "Various":
                    c1.setId(1L);
                    c1.setName(name);
                    c1.setDescription("All kinds of things");
                    break;
                case "Animal":
                    c1.setId(2L);
                    c1.setName(name);
                    c1.setDescription("Some animals");
                    break;
                case "House":
                    c1.setId(3L);
                    c1.setName(name);
                    c1.setDescription("Household items");
                    break;
                case "Fruit":
                    c1.setId(3L);
                    c1.setName(name);
                    c1.setDescription("Some popular fruits");
                    break;
                case "Number":
                    c1.setId(4L);
                    c1.setName(name);
                    c1.setDescription("Numbers 1 to 100");
                    break;
                case "Anything":
                    c1.setId(5L);
                    c1.setName(name);
                    c1.setDescription("Any kind of thing");
                    break;
                case "Week Day":
                    c1.setId(6L);
                    c1.setName(name);
                    c1.setDescription("Days of the week - Sunday to Saturday");
                    break;
            }
        }
        
        thing.setCategory(c1);
    }
}