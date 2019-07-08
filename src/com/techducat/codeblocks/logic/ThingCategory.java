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

    public class ThingCategory implements Serializable {

        private Long id;

        private ImageIcon icon;

        private String name;

        private String description;

        public ThingCategory() {
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
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "ThingCategory{" + "id=" + id + ", name=" + name + '}';
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.name);
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
            final ThingCategory other = (ThingCategory) obj;
            return Objects.equals(this.name, other.name);
        }
    }

