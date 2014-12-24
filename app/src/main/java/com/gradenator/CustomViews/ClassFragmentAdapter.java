package com.gradenator.CustomViews;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gradenator.Fragments.AllAssignmentsFragment;
import com.gradenator.Fragments.CalculateMinGradeFragment;
import com.gradenator.Fragments.ClassOverviewFragment;
import com.gradenator.Fragments.ViewGraphFragment;
import com.gradenator.Internal.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to display statistics of a single class.
 */
public class ClassFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mAllFragments;

    public ClassFragmentAdapter(FragmentManager fm) {
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
        mAllFragments.add(new ViewGraphFragment());
        mAllFragments.add(new CalculateMinGradeFragment());
    }

    public ClassOverviewFragment getClassOverViewFrag() {
        return (ClassOverviewFragment) mAllFragments.get(Constant.OVERVIEW_FRAGMENT);
    }
}
