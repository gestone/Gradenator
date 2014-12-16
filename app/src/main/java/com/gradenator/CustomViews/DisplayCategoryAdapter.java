package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gradenator.Internal.Category;
import com.gradenator.Internal.Constant;
import com.gradenator.Internal.Session;
import com.gradenator.Internal.Class;
import com.gradenator.R;
import com.gradenator.Utilities.Util;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter used in displaying the categories for the ClassOverViewFragment.
 */
public class DisplayCategoryAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Category> mAllCategories;
    private Activity mActivity;
    private CustomCirclePageIndicator mMainViewPager;

    public DisplayCategoryAdapter(Activity activity, List<Category> categories) {
        this.mAllCategories = new ArrayList<Category>();
        mAllCategories.addAll(categories); // total percentage not an actual category,
                                           // copy references
        int lightBlueColor = activity.getResources().getColor(R.color.card_text);
        Category totalScore = new Category(activity.getString(R.string.total_percentage), 100,
                Util.createRandomColor(activity));
        totalScore.setColor(lightBlueColor);
        mAllCategories.add(0, totalScore); // total score
        this.mActivity = activity;
    }

    public void setCirclePageIndicator(CustomCirclePageIndicator c) {
        mMainViewPager = c;
    }


    @Override
    public int getCount() {
        return mAllCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mActivity.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.display_category_entry, null);
        }
        setViewOnClickListener(convertView);
        Category currentCategory = mAllCategories.get(position);
        TextView categoryHeader = (TextView) convertView.findViewById(R.id.category_header);
        TextView categoryWeight = (TextView) convertView.findViewById(R.id.category_weight_tv);
        TextView numberOfAssignments = (TextView) convertView.findViewById(R.id
                .number_of_assignments);
        RoundedLetterView percentageBackground = (RoundedLetterView) convertView.findViewById(R.id
                .percentage_background);
        convertView.setTag(currentCategory.getTitle());
        categoryHeader.setText(currentCategory.getTitle());
        String weightMessage = currentCategory.getWeight() + mActivity.getResources().getString(R
                .string.weight_percentage);
        categoryWeight.setText(weightMessage);
        percentageBackground.setTitleText(weightMessage);
        percentageBackground.setBackgroundColor(currentCategory.getBackgroundColor());
        if (!currentCategory.getTitle().equals(mActivity.getString(R.string.total_percentage))) {
            percentageBackground.setTitleText(currentCategory.getPercentageDisplayHeader());
            numberOfAssignments.setText(currentCategory.getAssignmentDisplayText(mActivity));
        } else {
            Class c = Session.getInstance(mActivity).getCurrentClass();
            percentageBackground.setTitleText(c.getCardDisplayText());
            numberOfAssignments.setText(totalNumberAssignments());
        }
        return convertView;
    }

    private String totalNumberAssignments() {
        int total = 0;
        for (Category c : mAllCategories) {
            total += c.getAllAssignments().size();
        }
        String assignments = " " + mActivity.getString(R.string.all_assignments);
        if (total == 0) { // no assignments
            return mActivity.getString(R.string.no_assignments);
        } else if (total == 1) { // one assignment
            return total + assignments.substring(0, assignments.length() - 1);
        } else { // more than one assignment
            return total + assignments;
        }
    }

    // to be reimplemented
    private void setViewOnClickListener(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mMainViewPager.setCurrentItem(Constant.ALL_ASSIGNMENTS_FRAGMENT);
//                mMainViewPager.getAllAssignmentsFrag().setSpinner((String) v.getTag(), mActivity);
            }
        });
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
