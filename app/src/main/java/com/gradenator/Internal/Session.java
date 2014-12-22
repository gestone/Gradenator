package com.gradenator.Internal;

import android.app.Activity;
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
 * Created by Justin on 8/26/2014.
 */
public class Session {

    private List<Term> mAllTerms;
    private static Session mUserSession;
    private Term mCurrentTerm;
    private Class mCurrentClass;


    private Session() {
        mAllTerms = new ArrayList<Term>();
    }

    private Session(File f) {
        this();
        processJSONFile(f);
    }

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

    public void checkToRecordData() {
        if (mAllTerms != null) {
            Term t = mAllTerms.get(0); // most recent term
            long difference = System.currentTimeMillis() - t.getLastUpdateTime();
            if (difference > t.getUpdateInterval()) {
                t.recordClassPercentages();
            }
        }
    }

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

    private void processJSONFile(File f) {
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

    private void deleteBlankCategories() {
        if (mAllTerms != null && !mAllTerms.isEmpty()) {
            for (Term t : mAllTerms) {
                t.deleteAllBlankCategories();
            }
        }
    }


    public void deleteAllTerms(Activity a) {
        File f = new File(a.getFilesDir() + "/" + Util.deviceUDID(a));
        f.delete();
    }

}
