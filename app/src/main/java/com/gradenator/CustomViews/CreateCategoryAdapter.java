package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gradenator.Internal.Category;
import com.gradenator.R;

import java.util.List;

/**
 * Used in prompting the user to create a category.
 */
public class CreateCategoryAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Category> mAllCategories;

    public CreateCategoryAdapter(Activity a, List<Category> allCategories) {
        mAllCategories = allCategories;
        if (mAllCategories.isEmpty()) {
            mAllCategories.add(new Category(a)); // prompt for user to fill out first category
        }
        mInflater = (LayoutInflater) a.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
    }

    /**
     * ViewHolder pattern specified by Android guidelines.
     */
    private static class ViewHolder {
        public final EditText mCategoryTitle;
        public final EditText mCategoryWeight;

        public ViewHolder(EditText categoryTitle, EditText categoryWeight) {
            mCategoryTitle = categoryTitle;
            mCategoryWeight = categoryWeight;
        }
    }


    public List<Category> getCategoryList() {
        return mAllCategories;
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
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EditText categoryTitle;
        EditText categoryWeight;
        if (convertView == null) { // ViewHolder pattern, view has not been inflated.
            convertView = mInflater.inflate(R.layout.category_entry, null);
            categoryTitle = (EditText) convertView.findViewById(R.id.category_name);
            categoryWeight = (EditText) convertView.findViewById(R.id.category_weight);
            categoryWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override // need this so that it doesn't skip the title of the next row after enter
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return false;
                }
            });
            convertView.setTag(new ViewHolder(categoryTitle, categoryWeight));
        } else { // ViewHolder pattern, view has been inflated, get from ViewHolder object.
            ViewHolder vHolder = (ViewHolder) convertView.getTag();
            categoryTitle = vHolder.mCategoryTitle;
            categoryWeight = vHolder.mCategoryWeight;
        }
        setViews(categoryTitle, categoryWeight, mAllCategories.get(position));
        return convertView;
    }

    /**
     * Sets the category title and weight based on the values stored in the current category.
     */
    private void setViews(EditText categoryTitle, EditText categoryWeight, Category curCategory) {
        String title = curCategory.getTitle();
        double weight = curCategory.getWeight();
        categoryTitle.setText(title);
        if (weight == -1) {
            categoryWeight.setText("");
        } else {
            if (weight % 1 != 0) {
                categoryWeight.setText("" + weight);
            } else {
                categoryWeight.setText("" + (int) weight);
            }
        }
    }

    /**
     * Gets the view by the position specified in the adapter.
     * @param position The position at which the row should be obtained.
     * @param listView The ListView where the row should be obtained.
     * @return         A View representing the entire row encapsulating both the EditText
     *                 category title and the EditText category weight.
     */
    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}
