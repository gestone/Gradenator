package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gradenator.R;
import com.gradenator.Internal.Class;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Card used for displaying all the classes.
 */
public class ClassCard extends Card {

    private Class mClass;
    private Activity mActivity;

    private TextView mUnitCount;
    private TextView mPercentage;
    private RoundedLetterView mTermImage;

    public ClassCard(Class c, Activity activity, int innerLayout) {
        super(activity, innerLayout);
        mActivity = activity;
        mClass = c;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        mUnitCount = (TextView) parent.findViewById(R.id.unit_count);
        mPercentage = (TextView) parent.findViewById(R.id.percentage);
        mTermImage = (RoundedLetterView) parent.findViewById(R.id.card_image);
        mUnitCount.setText(constructTotalUnitsHeader());
        mPercentage.setText(mClass.getCardDisplayText());
        String className = mClass.getClassName();
        mTermImage.setBackgroundColor(mClass.getBackgroundColor());
        mTermImage.setTitleText(constructImageText(className));
        mTermImage.setTitleSize(80f);
    }

    private String constructTotalUnitsHeader() {
        Resources r = mActivity.getResources();
        int totalUnits = mClass.getUnitCount();
        String msg = totalUnits + " ";
        if (totalUnits == 1) {
            msg += r.getString(R.string.units_single);
        } else {
            msg += r.getString(R.string.units_multiple);
        }
        return msg;
    }

    /**
     * Constructs the RoundedLetterView's text.
     * @param className The title of the class name.
     * @return          A String representing what should be displayed to the user in the
     *                  RoundedCircleView.
     */
    private String constructImageText(String className) {
        String total;
        if (className.contains(" ")) {
            String[] split = className.split(" ");
            int leastLength = split[0].length();
            int index = 0;
            for (int i = 1; i < split.length; i++) {
                if (split[i].length() < leastLength || split[i].matches(".*\\d.*")) {
                    leastLength = split[i].length();
                    index = i;
                }
            }
            String chosen = split[index];
            if (chosen.length() > 4) {
                total = chosen.substring(0, 4);
            } else {
                total = chosen;
            }
        } else {
            if (className.length() >= 4) {
                total = className.substring(0, 4);
            } else {
                total = className;
            }
        }
        return total;
    }

    /**
     * Gets the class associated with this ClassCard.
     * @return The class associated with this ClassCard.
     */
    public Class getCorrespondingClass() {
        return mClass;
    }

}
