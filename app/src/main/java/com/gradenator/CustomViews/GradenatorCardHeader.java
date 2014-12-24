package com.gradenator.CustomViews;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gradenator.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Custom card header applied to all of the Cards in the Gradenator application.
 */
public class GradenatorCardHeader extends CardHeader {

    private String mTitle;
    private TextView mHeader;

    public GradenatorCardHeader(Context context, String title) {
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
