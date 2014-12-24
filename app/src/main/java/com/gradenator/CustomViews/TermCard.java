package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gradenator.Internal.Term;
import com.gradenator.R;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Custom card view that allows for the viewing of terms in the ViewTermsFragment.
 */
public class TermCard extends Card {

    private Term mCurrentTerm;
    private Activity mActivity;

    private TextView mUnitCount;
    private TextView mClassCount;
    private RoundedLetterView mTermImage;


    public TermCard(Term t, Activity activity, int innerLayout) {
        super(activity, innerLayout);
        mActivity = activity;
        mCurrentTerm = t;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        mUnitCount = (TextView) parent.findViewById(R.id.unit_count);
        mClassCount = (TextView) parent.findViewById(R.id.class_header);
        mTermImage = (RoundedLetterView) parent.findViewById(R.id.card_image);
        mUnitCount.setText(constructTotalUnitsHeader());
        mClassCount.setText(constructClassHeader());
        mTermImage.setBackgroundColor(mCurrentTerm.getBackgroundColor());
        String termName = mCurrentTerm.getTermName();
        mTermImage.setTitleSize(150f);
        mTermImage.setTitleText(termName.substring(0, 1)); // first letter for text
    }

    private String constructClassHeader() {
        Resources r = mActivity.getResources();
        String msg = mCurrentTerm.getAllClasses().size() + " ";
        if (mCurrentTerm.getAllClasses().size() != 1) {
            msg += r.getString(R.string.classes_multiple);
        } else {
            msg += r.getString(R.string.classes_single);
        }
        return msg;
    }

    private String constructTotalUnitsHeader() {
        Resources r = mActivity.getResources();
        int totalUnits = mCurrentTerm.getTotalUnits();
        String msg = totalUnits + " ";
        if (totalUnits == 1) {
            msg += r.getString(R.string.units_single);
        } else {
            msg += r.getString(R.string.units_multiple);
        }
        return msg;
    }
}