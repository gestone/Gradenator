package com.gradenator.CustomViews;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gradenator.Internal.Assignment;
import com.gradenator.R;

import de.hdodenhof.circleimageview.CircleImageView;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Justin on 9/18/2014.
 */
public class AssignmentCard extends Card {

    private Activity mActivity;
    private Assignment mAssignment;

    private TextView mDate;
    private TextView mCardText;
    private TextView mAssignmentPercentage;
    private CircleImageView mDateBackground;


    public AssignmentCard(Assignment a, Activity activity, int innerLayout) {
        super(activity, innerLayout);
        mActivity = activity;
        mAssignment = a;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        mDate = (TextView) parent.findViewById(R.id.assignment_date);
        mCardText = (TextView) parent.findViewById(R.id.card_text);
        mAssignmentPercentage = (TextView) parent.findViewById(R.id.assignment_percentage);
        mDateBackground = (CircleImageView) parent.findViewById(R.id.card_image);

        // setup color for image
        Rect rect = new Rect(0, 0, 75, 75);
        Bitmap image = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setColor(mAssignment.getBackgroundColor());
        canvas.drawRect(rect, paint);
        BitmapDrawable b = new BitmapDrawable(image);
        mDateBackground.setImageDrawable(b);

        updateCard();
    }

    public Assignment getAssignment() {
        return mAssignment;
    }

    public void updateCard() {
        mDate.setText(mAssignment.getDateCreated());
        mCardText.setText(mAssignment.createScoreMessage(mActivity));
        mAssignmentPercentage.setText(mAssignment.createPercentageText());
    }

}
