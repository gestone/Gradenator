package com.gradenator.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gradenator.CustomViews.ClassFragmentAdapter;
import com.gradenator.CustomViews.CustomCirclePageIndicator;
import com.gradenator.Internal.Class;
import com.gradenator.Internal.Constant;
import com.gradenator.Internal.Session;
import com.gradenator.R;
import com.gradenator.Utilities.Util;

/**
 * Displays information about a single class that the user has selected.
 */
public class ViewSingleClassFragment extends Fragment {

    public static final String TAG = ViewSingleClassFragment.class.getSimpleName();

    private ClassFragmentAdapter mAdapter;
    private ViewPager mViewPager;
    private CustomCirclePageIndicator mIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.class_layout, container, false);
        findAndSetViews(v);
        return v;
    }

    /**
     * Method to determine whether or not the ViewSingleClassFragment should be switched when the
     * back key is pressed.
     * @return If the user is currently on the ClassOverviewFragment, then the method will return true
     *         signaling the ViewSingleClassFragment to be switched out with another Fragment. Otherwise,
     *         the method will switch the user to the ClassOverViewFragment, return false,
     *         and not switch out.
     */
    public boolean shouldSwitch() {
        if (mViewPager.getCurrentItem() == Constant.OVERVIEW_FRAGMENT) {
            return true;
        } else {
            mViewPager.setCurrentItem(Constant.OVERVIEW_FRAGMENT);
            return false;
        }
    }

    /**
     * Finds and sets views from the inflated layout.
     * @param v The inflated layout containing all the views.
     */
    private void findAndSetViews(View v) {
        mAdapter = new ClassFragmentAdapter(getChildFragmentManager(), getActivity());
        mViewPager = (ViewPager) v.findViewById(R.id.class_pager);
        mViewPager.setAdapter(mAdapter);
        setCircleIndicator(v);
        mAdapter.setCirclePagerIndicator(mIndicator);
    }

    /**
     * Sets up the circle indicator at the bottom of the fragment.
     * @param v The inflated layout containing views, specifically, the circle indicator.
     */
    private void setCircleIndicator(View v) {
        mIndicator = (CustomCirclePageIndicator) v.findViewById(R.id.class_pager_indicator);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setFillColor(getResources().getColor(R.color.green_medium));
        mIndicator.setRadius(20);
        mIndicator.setStrokeColor(getResources().getColor(R.color.green_dark));
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int newPosition) {
                if (newPosition == Constant.OVERVIEW_FRAGMENT) {
                    mAdapter.getClassOverViewFrag().updateViews();
                } else if (newPosition == Constant.GRAPH_FRAGMENT) {
                    Util.hideSoftKeyboard(getActivity());
                }
                changeActionBar(newPosition);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mIndicator.setAllAssignmentsFrag((AllAssignmentsFragment) mAdapter.getItem(Constant
                .ALL_ASSIGNMENTS_FRAGMENT));
    }

    /**
     * Changes the action bar based on the position that
     * @param newPosition The new position that the ViewPager has been set to.
     */
    private void changeActionBar(int newPosition) {
        Class cur = Session.getInstance(getActivity()).getCurrentClass();
        String newTitle = cur.getClassName().trim() + " ";
        switch (newPosition) {
            case Constant.OVERVIEW_FRAGMENT:
                newTitle += getString(R.string.ab_overview);
                break;
            case Constant.ALL_ASSIGNMENTS_FRAGMENT:
                newTitle += getString(R.string.ab_assignments);
                break;
            case Constant.GRAPH_FRAGMENT:
                newTitle += getString(R.string.ab_graph);
                break;
            case Constant.CALCULATOR_FRAGMENT:
                newTitle += getString(R.string.ab_grade_calc);
                break;
        }
        Util.changeActionBarTitle(getActivity(), newTitle);
    }

}
