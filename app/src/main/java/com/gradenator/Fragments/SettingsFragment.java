package com.gradenator.Fragments;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import com.gradenator.R;

/**
 * Created by Justin on 11/17/2014.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);
    }
}
