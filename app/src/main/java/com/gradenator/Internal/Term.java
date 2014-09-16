package com.gradenator.Internal;

import android.graphics.Color;

import com.gradenator.Utilities.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 8/26/2014.
 */
public class Term {

    private String termName;
    private List<Class> allClasses;
    private String dateCreated;
    private int backgroundColor;

    public Term(String termName, long epochTime, int backgroundColor) {
        this.termName = termName;
        this.backgroundColor = backgroundColor;
        allClasses = new ArrayList<Class>();
        dateCreated = Util.createDate(epochTime);
    }

    public List<Class> getAllClasses() {
        return allClasses;
    }

    public void addClass(Class c) {
        allClasses.add(c);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean removeClass(String title) {
        for (int i = 0; i < allClasses.size(); i++) {
            if (allClasses.get(i).getClassName().equals(title)) {
                allClasses.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean classExists(String title) {
        for (Class c : allClasses) {
            if (c.getClassName().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public String getTermName() {
        return termName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public int getTotalUnits() {
        int total = 0;
        for (Class c : allClasses) {
            total += c.getUnitCount();
        }
        return total;
    }

}
