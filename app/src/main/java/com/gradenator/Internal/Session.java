package com.gradenator.Internal;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Justin on 8/26/2014.
 */
public class Session {

    private List<Term> mAllTerms;
    private static Session mUserSession;
    private static Activity mActivity;
    private Term mCurrentTerm;
    private Class mCurrentClass;


    private Session() {
        mAllTerms = new ArrayList<Term>();
    }

    private Session(File f) {
        this();
        processJSONFile(f);
    }

    public static Session getInstance(Activity a) {
        if (mUserSession == null) {
            mActivity = a;
            File sessionFile = new File(mActivity.getFilesDir() + "/session_file.json");
            if (sessionFile.exists()) {
                mUserSession = new Session(sessionFile);
            } else {
                mUserSession = new Session();
            }
        }
        return mUserSession;
    }

    public boolean hasNoTerms() {
        return mAllTerms.isEmpty();
    }

    public List<Term> getAllTerms() {
        return mAllTerms;
    }

    public Term getCurrentTerm() {
        return mCurrentTerm;
    }

    public void setCurrentTerm(Term selectedTerm) {
        mCurrentTerm = selectedTerm;
    }

    public void setCurrentClass(Class selectedClass) {
        mCurrentClass = selectedClass;
    }

    public Term findTerm(String termName) {
        for (Term t : mAllTerms) {
            if (t.getTermName().equals(termName)) {
                return t;
            }
        }
        return null;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
