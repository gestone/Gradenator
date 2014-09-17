package com.gradenator.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gradenator.Internal.Category;
import com.gradenator.Internal.Class;
import com.gradenator.Internal.Session;
import com.gradenator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all assignments for a given class.
 */
public class AllAssignmentsFragment extends Fragment {

    private TextView mHeader;
    private Spinner mFilter;
    private Class mClass;
    private RelativeLayout mAddAssignmentHeader;
    private Button mAddAssignment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_assignments_frag, container, false);
        findViews(v);
        return v;
    }

    private void findViews(View v) {
        mClass = Session.getInstance(getActivity()).getCurrentClass();
        mHeader = (TextView) v.findViewById(R.id.all_assignments_header);
        mHeader.setText(mClass.getClassName() + " " + getString(R.string.all_assignments));
        mFilter = (Spinner) v.findViewById(R.id.filter);
        List<String> allCategories = getCategoryTitles();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.filter_item, allCategories);
        mFilter.setAdapter(dataAdapter);
        mAddAssignmentHeader = (RelativeLayout) v.findViewById(R.id.add_button_header);
        mAddAssignment = (Button) v.findViewById(R.id.add_button);
    }

    private List<String> getCategoryTitles() {
        List<String> categoryTitles = new ArrayList<String>();
        List<Category> categories = mClass.getAllCategories();
        for (Category c : categories) {
            categoryTitles.add(c.getTitle());
        }
        return categoryTitles;
    }

}
