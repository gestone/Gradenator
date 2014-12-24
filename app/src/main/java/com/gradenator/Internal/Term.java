package com.gradenator.Internal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a user's school term. Keeps track of the user's classes for that term.
 */
public class Term {

    private String termName;
    private List<Class> allClasses;
    private long dateCreated;
    private int backgroundColor;
    private long updateInterval;

    /**
     * Sets the term from a JSONObject.
     * @param j The JSONObject that has the Terms.
     */
    public Term(JSONObject j) {
        allClasses = new ArrayList<Class>();
        setFromJSON(j);
    }

    /**
     * Constructor for creating a brand new Term.
     * @param termName The new term name.
     * @param color    The randomized color chosen from the pre-defined palette.
     */
    public Term(String termName, int color) {
        this.termName = termName;
        this.backgroundColor = color;
        allClasses = new ArrayList<Class>();
        dateCreated = System.currentTimeMillis();
        updateInterval = Constant.ONE_MINUTE; // one week before collecting data for graph again
    }

    /******************
     * GETTER/SETTERS *
     ******************/

    public List<Class> getAllClasses() {
        return allClasses;
    }

    public void addClass(Class c) {
        allClasses.add(c);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public int getTotalUnits() {
        int total = 0;
        for (Class c : allClasses) {
            total += c.getUnitCount();
        }
        return total;
    }

    public JSONObject getJSON() {
        try {
            JSONObject singleTerm = new JSONObject();
            JSONArray totalClasses = new JSONArray();
            singleTerm.put("term_name", termName);
            singleTerm.put("date_created", dateCreated);
            singleTerm.put("background_color", backgroundColor);
            singleTerm.put("all_classes", totalClasses);
            singleTerm.put("update_interval", updateInterval);
            for (Class c : allClasses) {
                totalClasses.put(c.getJSON());
            }
            return singleTerm;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setFromJSON(JSONObject j) {
        try {
            termName = j.getString("term_name");
            dateCreated = j.getLong("date_created");
            backgroundColor = j.getInt("background_color");
            updateInterval = j.getLong("update_interval");
            JSONArray allClasses = j.getJSONArray("all_classes");
            for (int i = 0; i < allClasses.length(); i++) {
                Class c = new Class(allClasses.getJSONObject(i));
                this.allClasses.add(c);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Deletes all the blank category each class might have.
     */
    public void deleteAllBlankCategories() {
        for (Class c : allClasses) {
            List<Category> allCategories = c.getAllCategories();
            Iterator<Category> itr = allCategories.iterator();
            while (itr.hasNext()) {
                if (itr.next().getTitle().length() == 0) {
                    itr.remove();
                }
            }
        }
    }

}
