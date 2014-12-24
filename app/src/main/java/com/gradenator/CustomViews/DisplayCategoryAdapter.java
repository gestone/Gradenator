package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gradenator.Internal.Category;
import com.gradenator.Internal.Session;
import com.gradenator.Internal.Class;
import com.gradenator.R;
import com.gradenator.Utilities.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter used in displaying the categories for the ClassOverviewFragment when the user is
 * focusing in on a particular class.
 */
public class DisplayCategoryAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Category> mAllCategories;
    private Activity mActivity;

    public DisplayCategoryAdapter(Activity a, List<Category> categories) {
        this.mAllCategories = new ArrayList<Category>();
        mAllCategories.addAll(categories); // total percentage not an actual category,
                                           // copy references
        int lightGreen = a.getResources().getColor(R.color.card_text);
        Category totalScore = new Category(a.getString(R.string.total_percentage), 100,
                Util.createRandomColor(a));
        totalScore.setColor(lightGreen);
        mAllCategories.add(0, totalScore); // total score, prompt first category
        mActivity = a;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * ViewHolder pattern specified by the Android guidelines.
     */
    private static class ViewHolder {
        public final TextView mCategoryHeader;
        public final TextView mCategoryWeight;
        public final TextView mNumberOfAssignments;
        public final RoundedLetterView mPercentageBackground;

        public ViewHolder(TextView catHead, TextView catWeight,
                          TextView numAssign, RoundedLetterView percentageBack) {
            mCategoryHeader = catHead;
            mCategoryWeight = catWeight;
            mNumberOfAssignments = numAssign;
            mPercentageBackground = percentageBack;
        }
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
        Category currentCategory = mAllCategories.get(position);
        TextView categoryHeader;
        TextView categoryWeight;
        TextView numberOfAssignments;
        RoundedLetterView percentageBackground;
        if (convertView == null) { // view hasn't been inflated before
            convertView = mInflater.inflate(R.layout.display_category_entry, null);
            categoryHeader = (TextView) convertView.findViewById(R.id.category_header);
            categoryWeight = (TextView) convertView.findViewById(R.id.category_weight_tv);
            numberOfAssignments = (TextView) convertView.findViewById(R.id.number_of_assignments);
            percentageBackground = (RoundedLetterView) convertView.findViewById(R.id
                    .percentage_background);
            convertView.setTag(new ViewHolder(categoryHeader, categoryWeight,
                    numberOfAssignments, percentageBackground));
        } else { // view has been inflated before, call from ViewHolder class
            ViewHolder holder = (ViewHolder) convertView.getTag();
            categoryHeader = holder.mCategoryHeader;
            categoryWeight = holder.mCategoryWeight;
            numberOfAssignments = holder.mNumberOfAssignments;
            percentageBackground = holder.mPercentageBackground;
        }
        setAllViews(currentCategory, categoryHeader, categoryWeight, numberOfAssignments,
                percentageBackground);
        return convertView;
    }

    /**
     * Sets all of the views to the fields specified by the current category.
     */
    private void setAllViews(Category currentCategory, TextView categoryHeader,
                             TextView categoryWeight, TextView numberOfAssignments,
                             RoundedLetterView percentageBackground) {
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
    }

    /**
     * Constructs a String for the total number of assignments stored in the category. The
     * message differs on the amount of assignments there are in the category.
     * @return If there are no assignments, "No Assignments" will be returned,
     * otherwise "#" + Assignment/Assignments will be returned.
     */
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


    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}