package com.gradenator.Internal;

import com.gradenator.Utilities.Util;

import java.util.ArrayList;
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
        this("", -1, Util.createRandomColor());
    }

    public Category(String title, int weight, int color) {
        this.title = title;
        this.weight = weight;
        this.color = color;
        allAssignments = new ArrayList<Assignment>();
    }

    public void addAssignment(String title, double earned, double total) {
        allAssignments.add(new Assignment(title, earned, total, System.currentTimeMillis()));
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
}
