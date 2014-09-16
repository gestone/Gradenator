package com.gradenator.Internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 8/25/2014.
 */
public class Class {

    private int unitCount;
    private String className;
    private List<Category> allCategories;

    public Class(String className, int unitCount) {
        this.className = className;
        this.unitCount = unitCount;
        allCategories = new ArrayList<Category>();
    }

    public boolean addCategory(String title, double weight) {
        if (canAddCategory()) {
            return allCategories.add(new Category(title, weight));
        } else {
            return false;
        }
    }

    private boolean canAddCategory() {
        double totalPercent = 0;
        for (Category c : allCategories) {
            totalPercent += c.getWeight();
        }
        return totalPercent < 100;
    }

    public boolean removeCategory(String title) {
        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getTitle().equals(title)) {
                allCategories.remove(i);
                return true;
            }
        }
        return false;
    }

    public String getClassName() {
        return className;
    }

    public int getUnitCount() {
        return unitCount;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
