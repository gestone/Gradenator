package com.gradenator.Internal;

import com.gradenator.Utilities.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 8/26/2014.
 */
public class Term {

    private String termName;
    private List<Class> allClasses;
    private long dateCreated;
    private int backgroundColor;

    public Term(JSONObject j) {
        allClasses = new ArrayList<Class>();
        setFromJSON(j);
    }

    public Term(String termName) {
        this.termName = termName;
        this.backgroundColor = Util.createRandomColor();
        allClasses = new ArrayList<Class>();
        dateCreated = System.currentTimeMillis();
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
            JSONArray allClasses = j.getJSONArray("all_classes");
            for (int i = 0; i < allClasses.length(); i++) {
                Class c = new Class(allClasses.getJSONObject(i));
                this.allClasses.add(c);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

}
