package com.gradenator.CustomViews;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gradenator.Internal.Assignment;
import com.gradenator.R;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Card used for displaying each individual Assignment.
 */
public class AssignmentCard extends Card {

    private Activity mActivity;
    private Assignment mAssignment;

    private TextView mCardText;
    private TextView mAssignmentPercentage;
    private RoundedLetterView mDateBackground;

    public AssignmentCard(Assignment a, Activity activity, int innerLayout) {
        super(activity, innerLayout);
        mActivity = activity;
        mAssignment = a;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        mCardText = (TextView) parent.findViewById(R.id.card_text);
        mAssignmentPercentage = (TextView) parent.findViewById(R.id.assignment_percentage);
        mDateBackground = (RoundedLetterView) parent.findViewById(R.id.card_image);
        updateCard();
    }

    public Assignment getAssignment() {
        return mAssignment;
    }

    public void updateCard() {
        mDateBackground.setTitleText(mAssignment.getDateCreated());
        mDateBackground.setBackgroundColor(mAssignment.getBackgroundColor());
        mCardText.setText(mAssignment.createScoreMessage(mActivity));
        mAssignmentPercentage.setText(mAssignment.createPercentageText());
    }

}
