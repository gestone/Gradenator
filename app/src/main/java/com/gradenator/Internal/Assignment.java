package com.gradenator.Internal;

import com.gradenator.Utilities.Util;

/**
 * Created by Justin on 8/25/2014.
 */
public class Assignment implements Comparable<Assignment> {

    private double earnedScore;
    private double maxScore;
    private String title;
    private long timeCreated;

    public Assignment(String title, double earnedScore, double maxScore, long timeCreated) {
        this.title = title;
        this.earnedScore = earnedScore;
        this.maxScore = maxScore;
        this.timeCreated = timeCreated;
    }

    public String getTitle() {
        return title;
    }

    public double getEarnedScore() {
        return earnedScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setEarnedScore(double earnedScore) {
        this.earnedScore = earnedScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateCreated() {
        return Util.createDate(timeCreated);
    }

    @Override
    public int compareTo(Assignment other) {
        if (this.timeCreated - other.timeCreated > 0) {
            return 1;
        } else { // not possible to have the assignments created at the same time
            return -1;
        }
    }
}
