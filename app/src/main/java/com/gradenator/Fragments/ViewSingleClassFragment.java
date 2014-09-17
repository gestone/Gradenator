package com.gradenator.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gradenator.CustomViews.ClassFragmentAdapter;
import com.gradenator.R;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Displays information about a single class that the user has selected.
 */
public class ViewSingleClassFragment extends Fragment {

    public static final String TAG = ViewSingleClassFragment.class.getSimpleName();

    private ClassFragmentAdapter mAdapter;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.class_layout, container, false);
        findAndSetViews(v);
        return v;
    }

    private void findAndSetViews(View v) {
        mAdapter = new ClassFragmentAdapter(getFragmentManager(), getActivity());
        mViewPager = (ViewPager) v.findViewById(R.id.class_pager);
        mViewPager.setAdapter(mAdapter);
        mIndicator = (CirclePageIndicator) v.findViewById(R.id.class_pager_indicator);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setFillColor(getResources().getColor(R.color.blue_light));
        mIndicator.setRadius(20);
        mIndicator.setStrokeColor(getResources().getColor(R.color.blue_dark));
    }

}
