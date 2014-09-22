package com.gradenator.Internal;

import com.gradenator.Utilities.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Justin on 8/25/2014.
 */
public class Class {

    private int unitCount;
    private String className;
    private int color;
    private List<Category> allCategories;

    public Class(JSONObject j) {
        setFromJSON(j);
    }

    public Class(String className, int unitCount, int color, List<Category> allCategories) {
        this.className = className;
        this.unitCount = unitCount;
        this.color = color;
        this.allCategories = allCategories;
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

    public int getBackgroundColor() {
        return color;
    }

    public double getCurrentPercentage() {
        double rawPercentage = 0;
        int totalWeight = 0;
        for (Category c : allCategories) {
            if (c.getWeightedPercentage() != -1.0) {
                rawPercentage += c.getWeightedPercentage();
                totalWeight += c.getWeight();
            }
        }
        double actualPercentage = rawPercentage / ((totalWeight * 1.0) / 100);
        return Util.roundToNDigits(actualPercentage, 2);
    }

    public String getCardDisplayText() {
        double rawPercentage = -1;
        int totalWeight = 0;
        for (Category c : allCategories) {
            if (c.getWeightedPercentage() != Constant.NO_ASSIGNMENTS) {
                if (rawPercentage == -1) {
                    rawPercentage = 0;
                }
                rawPercentage += c.getWeightedPercentage();
                totalWeight += c.getWeight();
            }
        }
        double actualPercentage = rawPercentage / ((totalWeight * 1.0) / 100);
        if (rawPercentage < 0) {
            return "% N/A";
        } else {
            return Util.roundToNDigits(actualPercentage, 2) + "%";
        }
    }

    public Category getCategory(String category) {
        for (Category c : allCategories) {
            if (c.getTitle().equals(category)) {
                return c;
            }
        }
        return null;
    }

    public Category getCategory(Assignment a) {
        for (Category c : allCategories) {
            if (c.getAllAssignments().contains(a)) {
                return c;
            }
        }
        return null;
    }

    public List<Category> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(List<Category> l) {
        allCategories = l;
    }

    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }

    public List<Assignment> getAllAssignments() {
        List<Assignment> allAssignments = new ArrayList<Assignment>();
        for (Category c : allCategories) {
            List<Assignment> assignments = c.getAllAssignments();
            if (assignments != null) {
                allAssignments.addAll(assignments);
            }
        }
        Collections.sort(allAssignments); // sorts all assignments by date created,
                                          // most recent first
        return allAssignments;
    }

    public Assignment findAssignment(String assignmentTitle) {
        List<Assignment> allAssignments = getAllAssignments();
        for (Assignment a : allAssignments) {
            if (a.getTitle().equals(assignmentTitle)) {
                return a;
            }
        }
        return null;
    }

    public JSONObject getJSON() {
        try {
            JSONObject singleClass = new JSONObject();
            JSONArray totalCategories = new JSONArray();
            singleClass.put("unit_count", unitCount);
            singleClass.put("class_name", className);
            singleClass.put("background_color", color);
            singleClass.put("all_categories", totalCategories);
            for (Category c : allCategories) {
                totalCategories.put(c.getJSON());
            }
            return singleClass;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setFromJSON(JSONObject j) {
        try {
            allCategories = new ArrayList<Category>();
            unitCount = j.getInt("unit_count");
            className = j.getString("class_name");
            color = j.getInt("background_color");
            JSONArray allCategories = j.getJSONArray("all_categories");
            for (int i = 0; i < allCategories.length(); i++) {
                Category c = new Category(allCategories.getJSONObject(i));
                this.allCategories.add(c);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
