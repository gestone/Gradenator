package com.gradenator.Internal;

import android.app.Activity;

import com.gradenator.R;
import com.gradenator.Utilities.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Justin on 8/25/2014.
 */
public class Assignment implements Comparable<Assignment> {

    private double earnedScore;
    private double maxScore;
    private String title;
    private long timeCreated;
    private int assignmentColor;

    public Assignment(JSONObject j) {
        setFromJSON(j);
    }

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
        if (maxScore % 1 == 0) { // is not a double
            int integerMaxScore = (int) maxScore;
            msg += integerMaxScore;
        } else { // it's a double
            msg += maxScore;
        }
        return msg;
    }

    public String createPercentageText() {
        double score = (earnedScore / maxScore) * 100;
        if (maxScore == 0) {
            score = earnedScore * 100;
        }
        return Util.roundToNDigits(score, 2) + "%";
    }

    public JSONObject getJSON() {
        try {
            JSONObject singleAssignment = new JSONObject();
            singleAssignment.put("earned_score", earnedScore);
            singleAssignment.put("max_score", maxScore);
            singleAssignment.put("title", title);
            singleAssignment.put("time_created", timeCreated);
            singleAssignment.put("assignment_color", assignmentColor);
            return singleAssignment;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setFromJSON(JSONObject j) {
        try {
            earnedScore = j.getDouble("earned_score");
            maxScore = j.getDouble("max_score");
            title = j.getString("title");
            timeCreated = j.getLong("time_created");
            assignmentColor = j.getInt("assignment_color");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
