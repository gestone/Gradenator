package com.gradenator.Utilities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.widget.Toast;

import com.gradenator.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by Justin on 8/24/2014.
 */
public class Util {

    /**
     * Restricts access.
     */
    private Util() {

    }

    public static void displayFragment(Fragment f, String tag, Activity a) {
        FragmentTransaction ft = a.getFragmentManager().beginTransaction().addToBackStack(tag);
        ft.replace(R.id.container, f).commit();
    }

    /**
     * Creates a String representing the current date in MM/dd/yyyy format.
     * @param epochTime The current epoch time.
     * @return          A String representing the date in MM/dd/yyyy format.
     */
    public static String createDate(long epochTime) {
        Date theDate = new Date(epochTime);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(theDate);
    }

    public static int createRandomColor() {
        Random r = new Random();
        return Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    public static void makeToast(Activity a, String text) {
        Toast.makeText(a, text, Toast.LENGTH_SHORT).show();
    }

    public static double roundToNDigits(double value, int n) {
        int roundTo = (int) Math.pow(10, n);
        return (double) Math.round(value * roundTo) / roundTo;
    }

}
