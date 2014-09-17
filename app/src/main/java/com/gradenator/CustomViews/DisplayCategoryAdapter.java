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
import com.gradenator.Internal.Session;
import com.gradenator.Internal.Class;
import com.gradenator.R;
import com.gradenator.Utilities.Util;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter used in displaying the categories for the ClassOverViewFragment.
 */
public class DisplayCategoryAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Category> mAllCategories;
    private Activity mActivity;

    public DisplayCategoryAdapter(Activity activity, List<Category> categories) {
        this.mAllCategories = new ArrayList<Category>();
        mAllCategories.addAll(categories);
        int blackColor = Color.argb(255, 0, 0, 0);
        mAllCategories.add(0, new Category(activity.getString(R.string.total_percentage), 100,
                blackColor)); // total score
        this.mActivity = activity;
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
        Category currentCategory = mAllCategories.get(position);
        TextView categoryHeader = (TextView) convertView.findViewById(R.id.category_header);
        TextView categoryWeight = (TextView) convertView.findViewById(R.id.category_weight);
        CircleImageView percentageBackground = (CircleImageView) convertView.findViewById(R.id
                .percentage_background);
        categoryHeader.setText(currentCategory.getTitle());
        TextView percentage = (TextView) convertView.findViewById(R.id.percentage);
        setCircleBackground(percentageBackground, currentCategory);
        String weightMessage = currentCategory.getWeight() + mActivity.getResources().getString(R
                .string.weight_percentage);
        categoryWeight.setText(weightMessage);
        if (!currentCategory.getTitle().equals(mActivity.getString(R.string.total_percentage))) {
            percentage.setText(currentCategory.getPercentageDisplayHeader());
        } else {
            Class c = Session.getInstance(mActivity).getCurrentClass();
            percentage.setText(c.getCardDisplayText());
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private void setCircleBackground(CircleImageView percentageBackground, Category c) {
        Rect rect = new Rect(0, 0, 75, 75);
        Bitmap image = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setColor(c.getBackgroundColor());
        canvas.drawRect(rect, paint);
        BitmapDrawable b = new BitmapDrawable(image);
        percentageBackground.setImageDrawable(b);
    }
}