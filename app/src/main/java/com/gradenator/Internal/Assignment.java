package com.gradenator.Internal;

/**
 * Created by Justin on 8/25/2014.
 */
public class Assignment {

    private double earnedScore;
    private double maxScore;
    private String title;

    public Assignment(String title, double earnedScore, double maxScore) {
        this.title = title;
        this.earnedScore = earnedScore;
        this.maxScore = maxScore;
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

}
