package com.gradenator.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.gradenator.Internal.*;
import com.gradenator.Internal.Class;
import com.gradenator.R;
import com.gradenator.Utilities.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Justin on 11/5/2014.
 */
public class CalculateMinGradeFragment extends Fragment {

    @InjectView(R.id.choose_category)
    Spinner mCategory;
    @InjectView(R.id.percentage_maintain)
    EditText mMaintain;
    @InjectView(R.id.maximum_points)
    EditText mMaxPoints;
    @InjectView(R.id.calculate)
    Button mCalculate;
    @InjectView(R.id.calculated_score_result)
    TextView mResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.min_grade_frag, container, false);
        ButterKnife.inject(this, layout);
        mResult.setVisibility(View.INVISIBLE);
        populateSpinner();
        return layout;
    }

    /**
     * Populates a spinner with all the categories for this particular class.
     */
    private void populateSpinner() {
        List<Category> allCategories = Session.getInstance(getActivity()).getCurrentClass()
                .getAllCategories();
        List<String> categories = new ArrayList<String>();
        for (Category c : allCategories) {
            categories.add(c.getTitle());
        }
        ArrayAdapter<String> categoryNames = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, categories);
        mCategory.setAdapter(categoryNames);
    }

    @OnClick(R.id.calculate)
    public void calculateGrade(Button b) {
        String category = mCategory.getSelectedItem().toString();
        if (!mMaxPoints.getText().toString().isEmpty() && !mMaintain.getText().toString().isEmpty
                ()) {
           calculatePoints(category);
        } else {
           createNoFieldsErrorDialog();
        }
    }

    private void createNoFieldsErrorDialog() {
        String title = getString(R.string.empty_field_title);
        String msg = getString(R.string.empty_fields_msg_min_grade);
        Util.createErrorDialog(title, msg, getActivity());
    }

    private void calculatePoints(String category) {
        double maxPoints = Double.parseDouble(mMaxPoints.getText().toString());
        double maintain = Double.parseDouble(mMaintain.getText().toString());
        Class cur = Session.getInstance(getActivity()).getCurrentClass();
        List<Category> allCategories = cur.getAllCategories();
        double otherWeightedCategories = 0;
        double totalDefinedCategoryPercentage = 0;
        Category selectedCategory = null;
        for (Category c : allCategories) {
            if (!c.getTitle().equals(category)) {
                if (c.getWeightedPercentage() != Constant.NO_ASSIGNMENTS) {
                    otherWeightedCategories += c.getWeightedPercentage();
                    totalDefinedCategoryPercentage += c.getWeight();
                }
            } else { // found the category that the assignment belongs to
                selectedCategory = c;
                totalDefinedCategoryPercentage += c.getWeight();
            }
        }
        totalDefinedCategoryPercentage /= 100;
        double percentageInSelected = (maintain * totalDefinedCategoryPercentage -
                otherWeightedCategories) / selectedCategory.getWeight();
        double neededPoints = 0;
        if (selectedCategory.hasNoAssignments()) {
            neededPoints = percentageInSelected * maxPoints;
        } else {
            double earnedPoints = 0;
            double maxCategoryPoints = 0;
            List<Assignment> allAssignments = selectedCategory.getAllAssignments();
            for (Assignment a : allAssignments) {
                earnedPoints += a.getEarnedScore();
                maxCategoryPoints += a.getMaxScore();
            }
            maxCategoryPoints += maxPoints;
            neededPoints = ((percentageInSelected * maxCategoryPoints) - earnedPoints);
        }
        neededPoints = Util.roundToNDigits(neededPoints, 2);
        String msg = getString(R.string.min_grade_msg_part_1) + " " + neededPoints + " out of " + maxPoints + " " + getString(R
                .string.min_grade_msg_part_2);
        mResult.setVisibility(View.VISIBLE);
        mResult.setText(msg);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
