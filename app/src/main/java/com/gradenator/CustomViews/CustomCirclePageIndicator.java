package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.gradenator.Fragments.AllAssignmentsFragment;
import com.viewpagerindicator.CirclePageIndicator;

/**
* Created by Justin on 9/18/2014.
*/
public class CustomCirclePageIndicator extends CirclePageIndicator {

    private AllAssignmentsFragment mAssignments;

    public CustomCirclePageIndicator(Context a) {
        super(a);
    }

    public CustomCirclePageIndicator(Context a, AttributeSet atr) {
        super(a, atr);
    }

    public CustomCirclePageIndicator(Context a, AttributeSet atr, int defStyle) {
        super(a, atr, defStyle);
    }

    public void setAllAssignmentsFrag(AllAssignmentsFragment fragment) {
        mAssignments = fragment;
    }

    public AllAssignmentsFragment getAllAssignmentsFrag() {
        return mAssignments;
    }

}
