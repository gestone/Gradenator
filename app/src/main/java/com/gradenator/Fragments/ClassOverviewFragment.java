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


    public ClassOverviewFragment() {
        mClass = Session.getInstance(getActivity()).getCurrentClass();
        Activity a = Session.getCurrentActivity();
        mDisplayCategories = new DisplayCategoryAdapter(a, mClass.getAllCategories());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.overview_fragment, container, false);
        findViews(v);
        return v;
    }

    private void findViews(View v) {
        mOverviewHeader = (TextView) v.findViewById(R.id.overview_image);
        mAllCategories = (ListView) v.findViewById(R.id.overview_categories);
        updateViews();
    }

    public void updateViews() {
        mOverviewHeader.setBackgroundColor(mClass.getBackgroundColor());
        mOverviewHeader.setText(mClass.getClassName().toUpperCase());
        mAllCategories.setAdapter(mDisplayCategories);
    }

    public DisplayCategoryAdapter getDisplayCategoryAdapter() {
        return mDisplayCategories;
    }
}
