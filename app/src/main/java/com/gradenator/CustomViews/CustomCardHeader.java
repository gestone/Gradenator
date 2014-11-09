package com.gradenator.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gradenator.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Justin on 11/6/2014.
 */
public class CustomCardHeader extends CardHeader {

    private String mTitle;
    private TextView mHeader;


    public CustomCardHeader(Context context, String title) {
        super(context, R.layout.card_header);
        mTitle = title;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        mHeader = (TextView) parent.findViewById(R.id.custom_card_header);
        mHeader.setText(mTitle);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
    }
}
