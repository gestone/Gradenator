package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gradenator.Internal.Term;
import com.gradenator.R;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Custom card view that allows for the viewing of terms
 */
public class TermCard extends Card {

    private Term mCurrentTerm;
    private Activity mActivity;

    private TextView mUnitCount;
    private TextView mClassCount;
    private CircleImageView mTermImage;
    private TextView mTermImageText;


    public TermCard(Term t, Activity activity, int innerLayout) {
        super(activity, innerLayout);
        mActivity = activity;
        mCurrentTerm = t;
        initCardView();
    }

    private void initCardView() {

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        // setup views
        mUnitCount = (TextView) parent.findViewById(R.id.unit_count);
        mClassCount = (TextView) parent.findViewById(R.id.class_header);
        mTermImage = (CircleImageView) parent.findViewById(R.id.card_image);
        Rect rect = new Rect(0, 0, 75, 75);

        // setup color
        Bitmap image = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setColor(mCurrentTerm.getBackgroundColor());
        canvas.drawRect(rect, paint);
        BitmapDrawable b = new BitmapDrawable(image);
        mTermImage.setImageDrawable(b);
        mTermImageText = (TextView) parent.findViewById(R.id.term_image_text);

        mUnitCount.setText(mCurrentTerm.getTotalUnits() + " " + mActivity.getResources()
                .getString(R.string.units_header));
        mClassCount.setText(constructClassHeader());
        String termName = mCurrentTerm.getTermName();
        mTermImageText.setText(termName.substring(0, 1)); // first letter for text
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

}
