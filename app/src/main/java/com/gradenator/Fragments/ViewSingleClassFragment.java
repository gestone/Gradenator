package com.gradenator.Fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gradenator.CustomViews.ClassFragmentAdapter;
import com.gradenator.CustomViews.CustomCirclePageIndicator;
import com.gradenator.Internal.Constant;
import com.gradenator.R;

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

    private void findAndSetViews(View v) {
        mAdapter = new ClassFragmentAdapter(getActivity().getFragmentManager(), getActivity());
        mViewPager = (ViewPager) v.findViewById(R.id.class_pager);
        mViewPager.setAdapter(mAdapter);
        mIndicator = (CustomCirclePageIndicator) v.findViewById(R.id.class_pager_indicator);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setFillColor(getResources().getColor(R.color.blue_light));
        mIndicator.setRadius(20);
        mIndicator.setStrokeColor(getResources().getColor(R.color.blue_dark));
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int newPosition) {
                if (newPosition == Constant.OVERVIEW_FRAGMENT) {
                    mAdapter.getClassOverViewFrag().updateViews();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mIndicator.setAllAssignmentsFrag((AllAssignmentsFragment) mAdapter.getItem(Constant
                .ALL_ASSIGNMENTS_FRAGMENT));
        mAdapter.setCirclePagerIndicator(mIndicator);
    }

}
