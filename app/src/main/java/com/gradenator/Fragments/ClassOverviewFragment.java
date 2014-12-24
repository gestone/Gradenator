package com.gradenator.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * Displays a class overview of a single instance of a Class.
 */
public class ClassOverviewFragment extends Fragment {

    private DisplayCategoryAdapter mDisplayCategories;
    private ListView mAllCategories;
    private TextView mOverviewHeader;
    private Class mClass;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        if (mClass == null) {
            mClass = Session.getInstance(a).getCurrentClass();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.overview_fragment, container, false);
        findViews(v);
        mClass = Session.getInstance(getActivity()).getCurrentClass();
        return v;
    }

    private void findViews(View v) {
        mOverviewHeader = (TextView) v.findViewById(R.id.overview_image);
        mAllCategories = (ListView) v.findViewById(R.id.overview_categories);
        mOverviewHeader.setText(mClass.getClassName().toUpperCase());
        mOverviewHeader.setBackgroundColor(mClass.getBackgroundColor());
        mAllCategories.setAdapter(getDisplayCategoryAdapter(getActivity()));
    }

    public void updateViews() {
        mDisplayCategories.notifyDataSetChanged();
    }

    public DisplayCategoryAdapter getDisplayCategoryAdapter(Activity a) {
        if (mDisplayCategories == null) { // first time creating
            mClass = Session.getInstance(a).getCurrentClass();
            mDisplayCategories = new DisplayCategoryAdapter(a, mClass.getAllCategories());
        }
        return mDisplayCategories;
    }

}
