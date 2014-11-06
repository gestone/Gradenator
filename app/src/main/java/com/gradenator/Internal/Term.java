package com.gradenator.Internal;

import com.gradenator.Utilities.Util;

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
    private long lastUpdateTime;
    private long updateInterval;

    public Term(JSONObject j) {
        allClasses = new ArrayList<Class>();
        setFromJSON(j);
    }

    public Term(String termName) {
        this.termName = termName;
        this.backgroundColor = Util.createRandomColor();
        allClasses = new ArrayList<Class>();
        dateCreated = System.currentTimeMillis();
        lastUpdateTime = dateCreated;
        updateInterval = Constant.ONE_MINUTE; // one week before collecting data for graph again
    }

    public List<Class> getAllClasses() {
        return allClasses;
    }

    public void addClass(Class c) {
        allClasses.add(c);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean removeClass(String title) {
        for (int i = 0; i < allClasses.size(); i++) {
            if (allClasses.get(i).getClassName().equals(title)) {
                allClasses.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean classExists(String title) {
        for (Class c : allClasses) {
            if (c.getClassName().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public String getTermName() {
        return termName;
    }

    public String getDateCreated() {
        return Util.createDate(dateCreated);
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public void recordClassPercentages() {
        for (Class c : allClasses) {
            c.recordData();
        }
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
