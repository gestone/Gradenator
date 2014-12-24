package com.gradenator.Utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.gradenator.Dialogs.GenericDialog;
import com.gradenator.MainActivity;
import com.gradenator.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Util class to be used throughout the application to avoid code redundancy.
 */
public class Util {

    /**
     * Restricts access.
     */
    private Util() {

    }

    /**
     * Displays a Fragment.
     * @param f     The Fragment to be displayed.
     * @param tag   The associated tag that is with the Fragment, usually the class name.
     * @param a     The FragmentActivity to have the Fragment switched out of.
     */
    public static void displayFragment(Fragment f, String tag, FragmentActivity a) {
        FragmentTransaction ft = a.getSupportFragmentManager().beginTransaction()
                .addToBackStack(tag);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right,
                R.anim.slide_in_left, R.anim.exit_right);
        ft.replace(R.id.container, f, tag).commit();
    }

    /**
     * Creates a String representing the current date in MM/dd/yyyy format.
     * @param epochTime The current epoch time.
     * @return A String representing the date in MM/dd/yyyy format.
     */
    public static String createDate(long epochTime) {
        Date theDate = new Date(epochTime);
        DateFormat df = new SimpleDateFormat("MM/dd");
        String date = df.format(theDate);
        if (date.startsWith("0")) {
            date = date.substring(1);
        }
        return date;
    }

    /**
     * Creates a random color from the defined colors.xml palette.
     * @param a The activity to be passed in to be able to reference the color palette.
     * @return  An integer representing the random color generated from the predefined color
     *          palette.
     */
    public static int createRandomColor(Activity a) {
        Random r = new Random();
        int[] colorPalette = a.getResources().getIntArray(R.array.color_palette);
        return colorPalette[r.nextInt(colorPalette.length)];
    }

    /**
     * Makes a toast to be displayed to the user.
     * @param c     The Context of the application.
     * @param text  The text to be displayed to the user.
     */
    public static void makeToast(Context c, String text) {
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns a double to round to the nth digit place.
     * @param value The double to be rounded.
     * @param n     The digit to be rounded to.
     * @return      A rounded double.
     */
    public static double roundToNDigits(double value, int n) {
        int roundTo = (int) Math.pow(10, n);
        return (double) Math.round(value * roundTo) / roundTo;
    }

    /**
     * Creates a generic error dialog to be displayed to the user.
     * @param title The title of the error dialog.
     * @param msg   The message of the error dialog to be displayed.
     * @param a     The activity where the error dialog should be displayed.
     */
    public static void createErrorDialog(String title, String msg, Activity a) {
        GenericDialog g = GenericDialog.newInstance(title, msg);
        Util.changeDialogColor(g.show(a), a);
    }

    /**
     * Generates the UUID associated with the device.
     * @param ctx The context of the application.
     * @return    The UUID associated with the device as a String.
     */
    public static String deviceUDID(Context ctx) {
        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        Log.d("Device Id", deviceId);
        return deviceId;
    }

    /**
     * Hides the soft keyboard from the user if it's open.
     * @param a The activity where the soft keyboard is.
     */
    public static void hideSoftKeyboard(Activity a) {
        if (a.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) a.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(a.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Changes the Action Bar's Title.
     * @param a        The activity where the action bar is.
     * @param newTitle The new title to change the action bar to.
     */
    public static void changeActionBarTitle(Activity a, String newTitle) {
        MainActivity m = (MainActivity) a;
        m.changeActionBar(newTitle);
    }

    /**
     * Hides all views passed in.
     * @param v The views to be hidden from the user.
     */
    public static void hideViews(View... v) {
        for (View view : v) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Displays all the views passed in.
     * @param v The views to be shown to the user.
     */
    public static void showViews(View... v) {
        for (View view : v) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Changes the dialog color to match the green theme.
     * @param d The dialog to be changed to the green theme.
     * @param c The Context of the application.
     */
    public static void changeDialogColor(Dialog d, Context c) {
        int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = d.findViewById(dividerId);
        divider.setBackgroundColor(c.getResources().getColor(R.color.green_medium));
        int textViewId = d.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        TextView tv = (TextView) d.findViewById(textViewId);
        tv.setTextColor(c.getResources().getColor(R.color.green_medium));
    }

}
