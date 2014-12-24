package com.gradenator.Internal;

import android.app.Activity;

import com.gradenator.R;
import com.gradenator.Utilities.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Category represents a segment of how a class is graded. A category has a title,
 * (ex: Homework), a weight (ex: 20), and a randomly associated color with the Category. A
 * Category also contains a list of Assignments.
 */
public class Category {

    private String title;
    private double weight;
    private int color;
    private List<Assignment> allAssignments;

    /**
     * Creates a new empty Category.
     * @param a The activity of the Category.
     */
    public Category(Activity a) {
        this("", -1, Util.createRandomColor(a));
    }

    /**
     * Creates a new Category from a saved JSONObject.
     * @param j The saved JSONObject.
     */
    public Category(JSONObject j) {
        allAssignments = new ArrayList<Assignment>();
        setFromJSON(j);
    }

    /**
     * Constructor without a color for comparison purposes.
     * @param title  The title of the category.
     * @param weight The weight of the category.
     */
    public Category(String title, double weight) {
        this(title, weight, 0);
    }

    /**
     * Creates a brand new category initiated by the user.
     * @param title  The title of the category.
     * @param weight The weight of the category.
     * @param color  The randomized color decided from the pre-defined palette.
     */
    public Category(String title, double weight, int color) {
        this.title = title;
        this.weight = weight;
        this.color = color;
        allAssignments = new ArrayList<Assignment>();
    }

    /*******************
     * GETTERS/SETTERS *
     *******************/

    public List<Assignment> getAllAssignments() {
        Collections.sort(allAssignments);
        return allAssignments;
    }

    public double getWeight() {
        return weight;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Adds a new assignment to the end of the Assignments list.
     * @param a The Assignment to be added to the Category list of assignments.
     */
    public void addAssignment(Assignment a) {
        allAssignments.add(a);
    }

    /**
     * Removes a specific assignment if it exists in the list of all Assignments.
     * @param title The title of the Assignment to be removed.
     * @return      The removed Assignment.
     */
    public Assignment removeAssignment(String title) {
        for (int i = 0; i < allAssignments.size(); i++) {
            if (allAssignments.get(i).getTitle().equals(title)) {
                return allAssignments.remove(i);
            }
        }
        return null;
    }

    /**
     * Gets the weighted percentage of the entire category in the overall scheme of the Category.
     * @return  A double representing the weighted percentage of the category.
     */
    public double getWeightedPercentage() {
        if (hasNoAssignments()) {
            return Constant.NO_ASSIGNMENTS;
        } else {
            double earnedPoints = 0;
            double totalPoints = 0;
            for (Assignment a : allAssignments) {
                earnedPoints += a.getEarnedScore();
                totalPoints += a.getMaxScore();
            }
            double weight = (this.weight * 1.0) / 100;
            return ((earnedPoints / totalPoints) * 100) * weight;
        }
    }

    /**
     * Gets the raw percentage of the entire category without factoring in the weight of the
     * Category.
     * @return  A double represented the unweighted percentage of the category.
     */
    private double getRawPercentage() {
        if (hasNoAssignments()) {
            return Constant.NO_ASSIGNMENTS;
        } else {
            double earnedPoints = 0;
            double totalPoints = 0;
            for (Assignment a : allAssignments) {
                earnedPoints += a.getEarnedScore();
                totalPoints += a.getMaxScore();
            }
            return Util.roundToNDigits((earnedPoints / totalPoints * 100), 2);
        }
    }

    /**
     * Gets the percentage display header
     * @return  A String representing the percentage display header when viewing Assignments.
     */
    public String getPercentageDisplayHeader() {
        if (hasNoAssignments()) {
            return "% N/A";
        } else {
            double rawPercentage = getRawPercentage();
            String withoutDot = (int) rawPercentage + "";
            if (withoutDot.length() > 3) {
                return withoutDot + "%";
            }
            return rawPercentage + "%";
        }
    }

    /**
     * Returns whether or not the Category has any assignments.
     * @return  A boolean representing whether or not there are any assignments.
     */
    public boolean hasNoAssignments() {
        return allAssignments == null || allAssignments.isEmpty();
    }

    /**
     * Gets the background color associated with the Category.
     * @return  Returns an integer representing the background color.
     */
    public int getBackgroundColor() {
        return color;
    }

    /**
     * Gets the Assignment display text for the Cards to be displayed to the user.
     * @param activity  The activity where the display text should be displayed.
     * @return          A String representing the Assignment display text to be shown to the user.
     */
    public String getAssignmentDisplayText(Activity activity) {
        if (allAssignments.size() == 0) {
            return activity.getResources().getString(R.string.no_assignments);
        } else {
            String msg = allAssignments.size() + " " + activity.getResources().getString(R.string
                    .assignments);
            if (allAssignments.size() == 1) {
                return msg.substring(0, msg.length() - 1); // 'assignments' to 'assignment'
            } else {
                return msg;
            }
        }
    }

    /**
     * Translates a Category into a JSONObject.
     * @return  The JSONObject representation of a Category.
     */
    public JSONObject getJSON() {
        try {
            JSONObject singleCategory = new JSONObject();
            JSONArray totalAssignments = new JSONArray();
            singleCategory.put("title", title);
            singleCategory.put("weight", weight);
            singleCategory.put("background_color", color);
            singleCategory.put("all_assignments", totalAssignments);
            for (Assignment a : allAssignments) {
                totalAssignments.put(a.getJSON());
            }
            return singleCategory;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the Category from a JSONObject.
     * @param j The JSONObject to set the Category to.
     */
    private void setFromJSON(JSONObject j) {
        try {
            title = j.getString("title");
            weight = j.getDouble("weight");
            color = j.getInt("background_color");
            JSONArray allAssignments = j.getJSONArray("all_assignments");
            for (int i = 0; i < allAssignments.length(); i++) {
                Assignment a = new Assignment(allAssignments.getJSONObject(i));
                this.allAssignments.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
