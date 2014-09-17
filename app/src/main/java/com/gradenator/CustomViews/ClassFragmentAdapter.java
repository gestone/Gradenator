package com.gradenator.CustomViews;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.gradenator.Fragments.AllAssignmentsFragment;
import com.gradenator.Fragments.ClassOverviewFragment;
import com.gradenator.Fragments.IntroFragment;
import com.gradenator.Internal.Class;
import com.gradenator.Internal.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 9/17/2014.
 */
public class ClassFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mAllFragments;

    public ClassFragmentAdapter(FragmentManager fm, Activity a) {
        super(fm);
        mAllFragments = new ArrayList<Fragment>();
        initializeClassFrags();
    }

    @Override
    public Fragment getItem(int position) {
        return mAllFragments.get(position);
    }

    @Override
    public int getCount() {
        return mAllFragments.size();
    }

    private void initializeClassFrags() {
        mAllFragments.add(new ClassOverviewFragment());
        mAllFragments.add(new AllAssignmentsFragment());
    }
}