package com.gradenator.Internal;

import android.app.Activity;

import com.gradenator.R;
import com.gradenator.Utilities.Util;

/**
 * Created by Justin on 8/25/2014.
 */
public class Assignment implements Comparable<Assignment> {

    private double earnedScore;
    private double maxScore;
    private String title;
    private long timeCreated;
    private int assignmentColor;

    public Assignment(String title, double earnedScore, double maxScore) {
        this.title = title;
        this.earnedScore = earnedScore;
        this.maxScore = maxScore;
        this.timeCreated = System.currentTimeMillis();
        this.assignmentColor = Util.createRandomColor();
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

    public int getBackgroundColor() {
        return assignmentColor;
    }

    @Override
    public int compareTo(Assignment other) {
        if (this.timeCreated - other.timeCreated > 0) {
            return -1;
        } else { // not possible to have the assignments created at the same time
            return 1;
        }
    }

    public String createScoreMessage(Activity a){
        String msg = "";
        if (earnedScore % 1 == 0) {
            int score = (int) earnedScore;
            msg += score;
        } else {
            msg += earnedScore;
        }
        msg += " " + a.getResources().getString(R.string.card_text_out_of) + " ";
        if (maxScore % 1 == 0) {
            int integerMaxScore = (int) maxScore;
            msg += integerMaxScore;
        } else {
            msg += maxScore;
        }
        return msg;
    }

    public String createPercentageText() {
        double score = (earnedScore / maxScore) * 100;
        return Util.roundToNDigits(score, 2) + "%";
    }
}
