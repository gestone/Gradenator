package com.gradenator.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gradenator.CustomViews.DisplayCategoryAdapter;
import com.gradenator.Internal.Session;
import com.gradenator.Internal.Class;
import com.gradenator.R;

/**
 * Created by Justin on 9/17/2014.
 */
public class ClassOverviewFragment extends Fragment {

    private DisplayCategoryAdapter mDisplayCategories;
    private ListView mAllCategories;
    private TextView mOverviewHeader;
    private Class mClass;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.overview_fragment, container, false);
        mClass = Session.getInstance(getActivity()).getCurrentClass();
        findViews(v);
        return v;
    }

    private void findViews(View v) {
        mOverviewHeader = (TextView) v.findViewById(R.id.overview_image);
        mOverviewHeader.setBackgroundColor(mClass.getBackgroundColor());
        mOverviewHeader.setText(mClass.getClassName().toUpperCase());
        mAllCategories = (ListView) v.findViewById(R.id.overview_categories);
        mDisplayCategories = new DisplayCategoryAdapter(getActivity(), mClass.getAllCategories());
        mAllCategories.setAdapter(mDisplayCategories);
    }



}
