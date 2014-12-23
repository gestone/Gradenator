package com.gradenator.Internal;

import android.app.Activity;

import com.gradenator.R;
import com.gradenator.Utilities.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used to keep track of a single user's assignment.
 *
 * An assignment contains both the user's earned and max score along with the title of the
 * assignment. This class also assigns a random color that is picked from the predefined color
 * palette in colors.xml along with a timestamp for when the user created the assignment.
 *
 * More importantly, this class implements the Comparable interface which allows for the
 * comparison of two Assignments. In an ordered list, the most recently created assignment will
 * be at the front of the list.
 */
public class Assignment implements Comparable<Assignment> {

    private double earnedScore;
    private double maxScore;
    private String title;
    private long timeCreated;
    private int assignmentColor;

    /**
     * Creates an Assignment from JSONObject saved in internal storage.
     * @param j The JSONObject to set the Assignment from.
     */
    public Assignment(JSONObject j) {
        setFromJSON(j);
    }

    /**
     * Constructor for an Assignment when the user creates a brand new Assignment.
     * @param title         The title of the Assignment.
     * @param earnedScore   The earned score for this Assignment.
     * @param maxScore      The max score for this Assignment.
     * @param color         The randomly generated color associated with this Assignment.
     */
    public Assignment(String title, double earnedScore, double maxScore, int color) {
        this.title = title;
        this.earnedScore = earnedScore;
        this.maxScore = maxScore;
        this.timeCreated = System.currentTimeMillis();
        this.assignmentColor = color;
    }

    /*******************
     * GETTERS/SETTERS *
     *******************/

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

    /**
     * Implementation of the Comparable interface.
     * @param other The other Assignment to compare 'this' to.
     * @return      An integer representing whether if 'this' was created later than 'other'. If
     *              this happens to be the case, -1 will be returned, otherwise 1 will be returned.
     */
    @Override
    public int compareTo(Assignment other) {
        if (this.timeCreated - other.timeCreated > 0) {
            return -1;
        } else { // not possible to have the assignments created at the same time
            return 1;
        }
    }

    /**
     * Creates a score message to be displayed to the user.
     * @param a The activity of the application to be displayed in.
     * @return  A String representing the the score message to be displayed to the user
     */
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

    /**
     * Creates a percentage text to be displayed on a Card to the user.
     * @return  A string representing the percentage text.
     */
    public String createPercentageText() {
        double score = (earnedScore / maxScore) * 100;
        if (maxScore == 0) {
            score = earnedScore * 100;
        }
        return Util.roundToNDigits(score, 2) + "%";
    }

    /**
     * Gets the JSON to be saved of Assignment.
     * @return  A JSONObject representing the entire assignment.
     */
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

    /**
     * Sets the Assignment from a JSONObject.
     * @param j A JSONObject representing a single assignment.
     */
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
