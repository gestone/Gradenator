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
 * Created by Justin on 8/25/2014.
 */
public class Category {

    private String title;
    private int weight;
    private int color;
    private List<Assignment> allAssignments;

    public Category() {
        this("", -1);
    }

    public Category(JSONObject j) {
        allAssignments = new ArrayList<Assignment>();
        setFromJSON(j);
    }

    public Category(String title, int weight) {
        this.title = title;
        this.weight = weight;
        this.color = Util.createRandomColor();
        allAssignments = new ArrayList<Assignment>();
    }

    public void addAssignment(Assignment a) {
        allAssignments.add(a);
    }

    public Assignment removeAssignment(String title) {
        for (int i = 0; i < allAssignments.size(); i++) {
            if (allAssignments.get(i).getTitle().equals(title)) {
                return allAssignments.remove(i);
            }
        }
        return null;
    }


    public List<Assignment> getAllAssignments() {
        Collections.sort(allAssignments);
        return allAssignments;
    }

    public int getWeight() {
        return weight;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setColor(int color) {
        this.color = color;
    }

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

    public String getPercentageDisplayHeader() {
        if (hasNoAssignments()) {
            return "% N/A";
        } else {
            return getRawPercentage() + "%";
        }
    }

    public boolean hasNoAssignments() {
        return allAssignments == null || allAssignments.isEmpty();
    }

    public int getBackgroundColor() {
        return color;
    }

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

    private void setFromJSON(JSONObject j) {
        try {
            title = j.getString("title");
            weight = j.getInt("weight");
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
