package com.gradenator;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;


import com.gradenator.Background.GradeUpdateReceiver;
import com.gradenator.Fragments.IntroFragment;
import com.gradenator.Fragments.ViewSingleClassFragment;
import com.gradenator.Fragments.ViewTermsFragment;
import com.gradenator.Internal.Constant;
import com.gradenator.Internal.Session;
import com.gradenator.Utilities.TypefaceUtil;
import com.gradenator.Utilities.Util;

import java.util.Calendar;

/**
 * Main and only Activity of Gradenator. Initializes important services on start up such as
 * setting up an alarm, choosing which Fragment will be displayed, and overriding the back button.
 */
public class MainActivity extends FragmentActivity {

    private PendingIntent mRecordGradeIntent;
    private AlarmManager mRecordGrade;
    private TextView mActionBarText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.custom_action_bar);
        TypefaceUtil.overrideFont(getApplicationContext(), "SANS_SERIF", Constant.DEFAULT_FONT);
        View customActionBar = getActionBar().getCustomView();
        Typeface tf = Typeface.createFromAsset(this.getAssets(), Constant.DEFAULT_FONT);
        mActionBarText = (TextView) customActionBar.findViewById(R.id.action_bar_text);
        mActionBarText.setTypeface(tf);
        mActionBarText.setText(getString(R.string.app_name));
        chooseFragment();
        checkAlarm();
    }

    /**
     * Used to start an alarm for the updating the grades class.
     */
    private void checkAlarm() {
        boolean alarmUp = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constant
                .ALARM_ON, false);
//        boolean alarmUp = false;
        if (!alarmUp) { // check if an alarm has already been set, if not, set it up
            Intent alarmIntent = new Intent(this, GradeUpdateReceiver.class);
            mRecordGradeIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            mRecordGrade = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            TimePicker t = new TimePicker(this);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, t.getCurrentHour());
            cal.set(Calendar.MINUTE, t.getCurrentMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            mRecordGrade.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    Constant.ONE_DAY, mRecordGradeIntent);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constant.ALARM_ON,
                    true).commit();
        }
    }

    /**
     * Chooses a Fragment to display during startup. If the user has any terms,
     * the ViewTermsFragment will be displayed as the user has terms, otherwise,
     * the IntroFragment will be displayed.
     */
    private void chooseFragment(){
        if (!Session.getInstance(this).hasTerms()) {
            Util.displayFragment(new IntroFragment(), IntroFragment.TAG,
                    this);
        } else {
            Util.displayFragment(new ViewTermsFragment(), ViewTermsFragment.TAG, this);
            mActionBarText.setText(getString(R.string.ab_all_terms));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Session.getInstance(this).saveTerms(this);
    }


    /**
     * Overriding of when the user presses the back button. If the user is viewing all their
     * terms and a term has been defined, the user will exit the application. However,
     * if the user has no terms defined, the user will go back to the introductory message. If a
     * user is viewing a single class and is not on the overview class screen,
     * the back button will redirect the user to the overview class screen,
     * otherwise it will function as a normal back button to go back to the last fragment on the
     * backstack.
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentOnBackStack();
        if (fragment instanceof ViewSingleClassFragment) {
            ViewSingleClassFragment frag = (ViewSingleClassFragment) fragment;
            mActionBarText.setText(getString(R.string.ab_all_terms));
            if (frag.shouldSwitch()) {
                getSupportFragmentManager().popBackStack();
                String allClassTitle = Session.getInstance(this).getCurrentTerm().getTermName() +
                        " " + getString(R.string.ab_classes);
                mActionBarText.setText(allClassTitle);
            }
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1 || termAlreadyCreated
                    (fragment)) {
                this.finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    /**
     * Checks if a term has already been created by the user.
     * @param f The Fragment that is on currently on the back stack.
     * @return  A boolean representing whether or not if the user is on the ViewTermsFragment and
     *          if the user has defined any terms.
     */
    private boolean termAlreadyCreated(Fragment f) {
        return f instanceof ViewTermsFragment && Session.getInstance(this).hasTerms();
    }

    /**
     * Used to get the Fragment currently sitting on the back stack.
     * @return  The Fragment on the back stack.
     */
    private Fragment getFragmentOnBackStack() {
        FragmentManager.BackStackEntry backEntry = getSupportFragmentManager()
                .getBackStackEntryAt(this.getSupportFragmentManager().getBackStackEntryCount() - 1);
        String str = backEntry.getName();
        return getSupportFragmentManager().findFragmentByTag(str);
    }

    /**
     * Changes the title of the ActionBar.
     * @param newTitle  The new title to be changed to on the ActionBar.
     */
    public void changeActionBar(String newTitle) {
        mActionBarText.setText(newTitle);
    }

}
