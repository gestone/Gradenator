package com.gradenator.Internal;

import com.gradenator.Utilities.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Class in the context of Gradenator is an object that keeps track how many units the class
 * is, the class name along with a list of all of the categories that make up the grading scheme
 * of the class. The class also keeps track of a color that is completely randomized and defined
 * from the pre-defined palette of the colors.xml as well as DataPoints that are used in graphing
 * the
 */
public class Class {

    private int unitCount;
    private String className;
    private int color;
    private List<Category> allCategories;
    private List<DataPoint> allDataPoints;

    /**
     * Creates an empty class.
     */
    public Class() {
        allCategories = new ArrayList<Category>();
        allDataPoints = new ArrayList<DataPoint>();
    }

    /**
     * Constructor for creating a class from a JSONObject.
     * @param j The JSONObject to set the Class from.
     */
    public Class(JSONObject j) {
        this();
        setFromJSON(j);
    }

    /**
     * Constructor for creating a brand new class.
     * @param className     The name of the new Class.
     * @param unitCount     The amount of units the Class is.
     * @param color         The color associated with the Class.
     * @param allCategories A List of all of the Categories pertaining to the Class.
     */
    public Class(String className, int unitCount, int color, List<Category> allCategories) {
        this();
        this.className = className;
        this.unitCount = unitCount;
        this.color = color;
        this.allCategories = allCategories;
    }


    /*******************
     * GETTERS/SETTERS *
     *******************/
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

    public List<Category> getAllCategories() {
        return allCategories;
    }

    public List<DataPoint> getDataPoints() {
        return allDataPoints;
    }

    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }

    public double getCurrentPercentage() {
        double rawPercentage = 0;
        int totalWeight = 0;
        for (Category c : allCategories) {
            if (c.getWeightedPercentage() != Constant.NO_ASSIGNMENTS) {
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
            double roundedPercentage = Util.roundToNDigits(actualPercentage, 2);
            String wholeNumberPercentage = (int) roundedPercentage + "";
            if (wholeNumberPercentage.length() > 3) {
                return (int) roundedPercentage + "%";
            }
            return roundedPercentage + "%";
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

    public JSONObject getJSON() {
        try {
            JSONObject singleClass = new JSONObject();
            JSONArray totalCategories = new JSONArray();
            JSONArray totalDataPoints = new JSONArray();
            singleClass.put("unit_count", unitCount);
            singleClass.put("class_name", className);
            singleClass.put("background_color", color);
            singleClass.put("all_categories", totalCategories);
            singleClass.put("all_data_points", totalDataPoints);
            for (Category c : allCategories) {
                totalCategories.put(c.getJSON());
            }
            for (DataPoint d : allDataPoints) {
                JSONObject dataPoint = new JSONObject();
                dataPoint.put("percentage", d.getPercentage());
                dataPoint.put("time_recorded", d.getTimeRecorded());
                totalDataPoints.put(dataPoint);
            }
            return singleClass;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setFromJSON(JSONObject j) {
        try {
            unitCount = j.getInt("unit_count");
            className = j.getString("class_name");
            color = j.getInt("background_color");
            JSONArray allCategories = j.getJSONArray("all_categories");
            JSONArray allDataPoints = j.getJSONArray("all_data_points");
            for (int i = 0; i < allCategories.length(); i++) {
                Category c = new Category(allCategories.getJSONObject(i));
                this.allCategories.add(c);
            }
            for (int k = 0; k < allDataPoints.length(); k++) {
                JSONObject dataPoint = allDataPoints.getJSONObject(k);
                DataPoint d = new DataPoint(dataPoint.getDouble("percentage"),
                        dataPoint.getLong("time_recorded"));
                this.allDataPoints.add(d);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets all the categories from a list of updated Categories.
     * @param updatedCategories The new list of updated Categories.
     */
    public void setAllCategories(List<Category> updatedCategories) {
        allCategories = updatedCategories;
    }

    /**
     * Gets all of the assignments associated with this class.
     * @return A list of Assignments contained in the class.
     */
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

    /**
     * Finds an Assignment in a class given an assignment title.
     * @param assignmentTitle The title of the Assignment.
     * @return                If the Assignment is in the class, it will return that Assignment,
     *                        otherwise it will return null.
     */
    public Assignment findAssignment(String assignmentTitle) {
        List<Assignment> allAssignments = getAllAssignments();
        for (Assignment a : allAssignments) {
            if (a.getTitle().equals(assignmentTitle)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Records the current grade of a certain Class.
     */
    public void recordData() {
        DataPoint d = new DataPoint(getCurrentPercentage(), System.currentTimeMillis());
        allDataPoints.add(d);
    }

}
