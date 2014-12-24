package com.gradenator.Internal;

import android.content.Context;

import com.gradenator.Utilities.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Singleton class used to keep track of the user's Session. In particular,
 * a Session keeps track of the user's current term along with which class the user currently has
 * selected. It is often used to be able to search for a particular term.
 */
public class Session {

    private List<Term> mAllTerms;
    private static Session mUserSession;
    private Term mCurrentTerm;
    private Class mCurrentClass;

    /**
     * Constructor used if no file exists.
     */
    private Session() {
        mAllTerms = new ArrayList<Term>();
    }

    /**
     * Constructor used if a file does exist.
     * @param f The File containing all of user's Terms.
     */
    private Session(File f) {
        this();
        setFromJSON(f);
    }

    /**
     * Reads in a file and returns a String.
     * @param f The file from internal storage.
     * @return  A String representing the entire file.
     */
    private String readJSONFile(File f) {
        String total = "";
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                total += s.nextLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Sets the Session from the JSON saved in internal storage.
     * @param f The file from internal storage.
     */
    private void setFromJSON(File f) {
        try {
            JSONObject file = new JSONObject(readJSONFile(f));
            JSONArray allTerms = file.getJSONArray("all_terms");
            for (int i = 0; i < allTerms.length(); i++) {
                Term t = new Term(allTerms.getJSONObject(i));
                mAllTerms.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the terms to the Session if the terms exists.
     * @param c The context of the application.
     */
    public void saveTerms(Context c) {
        deleteBlankCategories();
        try {
            JSONObject allData = new JSONObject();
            JSONArray allTerm = new JSONArray();
            allData.put("all_terms", allTerm);
            for (Term t : mAllTerms) {
                allTerm.put(t.getJSON());
            }
            if (!mAllTerms.isEmpty()) {
                File session = new File(c.getFilesDir() + "/" + Util.deviceUDID(c));
                PrintStream p = new PrintStream(session);
                p.println(allData);
                p.flush();
                p.close();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all the blank categories that the user may have in creating a class.
     */
    private void deleteBlankCategories() {
        if (mAllTerms != null && !mAllTerms.isEmpty()) {
            for (Term t : mAllTerms) {
                t.deleteAllBlankCategories();
            }
        }
    }

    /*******************
     * GETTERS/SETTERS *
     *******************/

    public static Session getInstance(Context c) {
        if (mUserSession == null) {
            File sessionFile = new File(c.getFilesDir() + "/" + Util.deviceUDID(c));
            if (sessionFile.exists()) {
                mUserSession = new Session(sessionFile);
            } else {
                mUserSession = new Session();
            }
        }
        return mUserSession;
    }

    public boolean hasTerms() {
        return !mAllTerms.isEmpty();
    }

    public List<Term> getAllTerms() {
        return mAllTerms;
    }

    public List<String> getAllTermNames() {
        List<String> allTermNames = new ArrayList<String>();
        for (Term t : mAllTerms) {
            allTermNames.add(t.getTermName());
        }
        return allTermNames;
    }

    public Term getCurrentTerm() {
        if (mCurrentTerm == null && !mAllTerms.isEmpty()) {
            return mAllTerms.get(0);
        }
        return mCurrentTerm;
    }

    public void setCurrentTerm(Term selectedTerm) {
        mCurrentTerm = selectedTerm;
    }

    public void setCurrentClass(Class selectedClass) {
        mCurrentClass = selectedClass;
    }

    public Class getCurrentClass() {
        return mCurrentClass;
    }

    public Term findTerm(String termName) {
        for (Term t : mAllTerms) {
            if (t.getTermName().equals(termName)) {
                return t;
            }
        }
        return null;
    }
}
