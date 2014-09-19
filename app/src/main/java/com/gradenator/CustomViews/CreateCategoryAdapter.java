package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gradenator.Internal.Category;
import com.gradenator.R;
import com.gradenator.Utilities.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in prompting the user to create a category.
 */
public class CreateCategoryAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Category> mAllCategories;
    private Activity mActivity;

    public CreateCategoryAdapter(Activity a, List<Category> allCategories) {
        mAllCategories = allCategories;
        if (mAllCategories.isEmpty()) {
            mAllCategories.add(new Category()); // prompt for user to fill out first category
        }
        mActivity = a;
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mActivity.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.category_entry, null);
        }
        final EditText categoryTitle = (EditText) convertView.findViewById(R.id.category_name);
        final EditText categoryWeight = (EditText) convertView.findViewById(R.id.category_weight);
        String title = mAllCategories.get(position).getTitle();
        int weight = mAllCategories.get(position).getWeight();
        if (!title.isEmpty()) { // if it already exists...
            categoryTitle.setText(title);
        }
        if (weight != -1) {
            categoryWeight.setText(weight + "");
        }
        categoryWeight.addTextChangedListener(new TextWatcher() {
            private boolean flag = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String numbers = categoryWeight.getText().toString();
                if (numbers.length() > 3 && !flag) {
                    categoryWeight.setText(numbers.substring(0, 3));
                    categoryWeight.setSelection(3);
                    flag = true;
                } else if (flag) {
                    flag = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return convertView;
    }

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
