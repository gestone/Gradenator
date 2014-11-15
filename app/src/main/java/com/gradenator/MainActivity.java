package com.gradenator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;


import com.gradenator.Background.GradeUpdateReceiver;
import com.gradenator.CustomViews.CustomTypefaceSpan;
import com.gradenator.Fragments.IntroFragment;
import com.gradenator.Fragments.MenuFragment;
import com.gradenator.Fragments.ViewSingleClassFragment;
import com.gradenator.Fragments.ViewTermsFragment;
import com.gradenator.Internal.Constant;
import com.gradenator.Internal.Session;
import com.gradenator.Utilities.TypefaceUtil;
import com.gradenator.Utilities.Util;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;



public class MainActivity extends SlidingFragmentActivity {

    private SlidingMenu mSlidingMenu;
    private PendingIntent mRecordGradeIntent;
    private AlarmManager mRecordGrade;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        setBehindContentView(R.layout.intro_frag);
        setupActionBar();
        setupSlidingMenu();
        chooseFragment();
        TypefaceUtil.overrideFont(getApplicationContext(), "SANS_SERIF", Constant.DEFAULT_FONT);
        boolean alarmUp = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constant
                .ALARM_ON, false);
        if (!alarmUp) { // check if an alarm has already been set
            startAlarm();
        }
    }

    private void setupActionBar() {
        SpannableStringBuilder newTitle = new SpannableStringBuilder(getString(R.string.app_name));
        Typeface customFontTypeface = Typeface.createFromAsset(this.getAssets(),
                "quicksandregular.ttf");
        newTitle.setSpan(new CustomTypefaceSpan("", customFontTypeface), 0, newTitle.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(newTitle);
    }


    /**
     * Used to start an alarm for the updating the grades class.
     */
    private void startAlarm() {
        Intent alarmIntent = new Intent(this, GradeUpdateReceiver.class);
        mRecordGradeIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        mRecordGrade = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mRecordGrade.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                Constant.ONE_WEEK, mRecordGradeIntent);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constant.ALARM_ON,
                true).commit();
    }

    private void setupSlidingMenu() {
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mSlidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setMenu(R.layout.sliding_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.list_layout,
                new MenuFragment()).commit();
        setSlidingActionBarEnabled(true);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

    private void chooseFragment(){
        if (!Session.getInstance(this).hasTerms()) {
            Util.displayFragment(new IntroFragment(), IntroFragment.TAG,
                    this);
        } else {
            Util.displayFragment(new ViewTermsFragment(), ViewTermsFragment.TAG, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getSlidingMenu().toggle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Session.getInstance(this).saveTerms(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session.getInstance(this); // refresh if it is not in memory
    }

    @Override
    public void onBackPressed() {
        FragmentManager.BackStackEntry backEntry= getSupportFragmentManager().getBackStackEntryAt(this
                .getSupportFragmentManager().getBackStackEntryCount() - 1);
        String str= backEntry.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(str);
        if (fragment instanceof ViewSingleClassFragment) {
            ViewSingleClassFragment frag = (ViewSingleClassFragment) fragment;
            if (frag.shouldSwitch()) {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                this.finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

}
