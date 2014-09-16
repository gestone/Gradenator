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

    private List<Term> allTerms;
    private static Session userSession;
    private static Activity activity;

    private Session() {
        allTerms = new ArrayList<Term>();
    }

    private Session(File f) {
        this();
        processJSONFile(f);
    }

    public static Session getInstance(Activity a) {
        if (userSession == null) {
            activity = a;
            File sessionFile = new File(activity.getFilesDir() + "/session_file.json");
            if (sessionFile.exists()) {
                userSession  = new Session(sessionFile);
            } else {
                userSession = new Session();
            }
        }
        return userSession;
    }

    public boolean hasNoTerms() {
        return allTerms.isEmpty();
    }

    public List<Term> getAllTerms() {
        return allTerms;
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
