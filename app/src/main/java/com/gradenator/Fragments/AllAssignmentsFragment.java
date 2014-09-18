package com.gradenator.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gradenator.Action;
import com.gradenator.Dialogs.GenericDialog;
import com.gradenator.Internal.Assignment;
import com.gradenator.Internal.Category;
import com.gradenator.Internal.Class;
import com.gradenator.Internal.Session;
import com.gradenator.R;
import com.gradenator.Utilities.Util;

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
    private String mSelectedCategory;

    private EditText mAssignmentTitleET;
    private EditText mEarnedPointsET;
    private EditText mMaxPointsET;
    private Spinner mAssignmentCategory;

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
        populateSpinner(mFilter);
        mAddAssignmentHeader = (RelativeLayout) v.findViewById(R.id.add_button_header);
        mAddAssignment = (Button) v.findViewById(R.id.add_button);
        mAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAssignmentDialog(Action.ADD);
            }
        });
        mAddAssignmentHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAssignmentDialog(Action.ADD);
            }
        });
    }

    private List<String> getCategoryTitles() {
        List<String> categoryTitles = new ArrayList<String>();
        List<Category> categories = mClass.getAllCategories();
        for (Category c : categories) {
            categoryTitles.add(c.getTitle());
        }
        return categoryTitles;
    }

    private void populateSpinner(Spinner s) {
        List<String> allCategories = getCategoryTitles();
        allCategories.add(0, getString(R.string.select_prompt));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.filter_item, allCategories);
        s.setAdapter(dataAdapter);
    }

    private void createAssignmentDialog(final Action action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (action == Action.ADD) {
            builder.setTitle(getString(R.string.create_assignment_header));
        } else if (action == Action.EDIT) {
            builder.setTitle(getString(R.string.edit_assignment_header));
        }
        LayoutInflater l = (LayoutInflater) getActivity().getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View v = l.inflate(R.layout.create_assignment, null);
        builder.setView(v);
        setupInflatedViews(v);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.create_assignment_header), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button createAssignment = d.getButton(AlertDialog.BUTTON_POSITIVE);
                createAssignment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String assignmentTitle = mAssignmentTitleET.getText().toString();
                        String earnedPoints = mEarnedPointsET.getText().toString();
                        String maxPoints = mMaxPointsET.getText().toString();
                        String selectedCategory = mAssignmentCategory.getSelectedItem().toString();
                        if (fieldsAreValid(assignmentTitle)) {
                            if (action == Action.ADD) {
                                try {
                                    double parsedEarnedPoints = Double.parseDouble(earnedPoints);
                                    double parsedMaxPoints = Double.parseDouble(maxPoints);
                                    mClass.findCategory(selectedCategory).addAssignment
                                            (assignmentTitle, parsedEarnedPoints,
                                                    parsedMaxPoints);
                                    Util.makeToast(getActivity(), getString(R.string.assignment_success_msg));
                                    d.dismiss();
                                } catch (NumberFormatException e) {
                                    createDialog(getString(R.string.points_invalid_format_title),
                                            getString(R.string.points_invalid_format_msg));
                                }
                            } else if (action == Action.EDIT) { // update card view

                            }
                        }
                    }
                });
            }
        });
        d.show();
    }

    private void setupInflatedViews(View v) {
        mAssignmentTitleET = (EditText) v.findViewById(R.id.assignment_title);
        mEarnedPointsET = (EditText) v.findViewById(R.id.earned_points);
        mMaxPointsET = (EditText) v.findViewById(R.id.max_points);
        mAssignmentCategory = (Spinner) v.findViewById(R.id.assignment_category);
        populateSpinner(mAssignmentCategory);
    }

    private boolean fieldsAreValid(String assignmentTitle) {
        if (areFieldsEmpty()) {
            createDialog(getString(R.string.empty_field_title),
                    getString(R.string.empty_field_error_msg));
            return false;
        } else if (duplicateTitles(assignmentTitle)) {
            createDialog(getString(R.string.duplicate_title_header), getString(R.string.duplicate_title_msg));
            return false;
        }
        return true;
    }

    private boolean areFieldsEmpty() {
        return mAssignmentTitleET.getText().toString().isEmpty() || mEarnedPointsET.getText()
                .toString().isEmpty() || mMaxPointsET.getText().toString().isEmpty() ||
                mAssignmentCategory.getSelectedItem().equals(getString(R.string.select_prompt));
    }

    private boolean duplicateTitles(String assignmentTitle) {
        List<Assignment> allAssignments = mClass.getAllAssignments();
        for (Assignment a : allAssignments) {
            if (a.getTitle().equals(assignmentTitle)) {
                return true;
            }
        }
        return false;
    }

    private void createDialog(String title, String msg) {
        GenericDialog g = GenericDialog.newInstance(title, msg);
        g.show(getFragmentManager(), GenericDialog.TAG);
    }

    public void setSpinner(String selected) {
        mFilter.setSelection(getCategoryTitles().indexOf(selected) + 1);
    }

}
