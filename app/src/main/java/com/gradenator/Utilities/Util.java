package com.gradenator.Utilities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.gradenator.Dialogs.GenericDialog;
import com.gradenator.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Util class to be used throughout the application.
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
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        String date = df.format(theDate);
        if (date.startsWith("0")) {
            date = date.substring(1);
        }
        return date;
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

    public static void createErrorDialog(String title, String msg, Activity a) {
        GenericDialog g = GenericDialog.newInstance(title, msg);
        g.show(a.getFragmentManager(), GenericDialog.TAG);
    }

    public static void setFocus(View v, Activity a) {
        v.requestFocus();
        InputMethodManager input = (InputMethodManager) a.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        input.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    public static String deviceUDID(Context ctx) {
        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" +android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        Log.d("Device Id", deviceId);
        return deviceId;
    }

}
