package com.gradenator.CustomViews;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gradenator.R;
import com.gradenator.Internal.Class;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Justin on 9/17/2014.
 */
public class ClassCard extends Card {

    private Class mClass;
    private Activity mActivity;

    private TextView mUnitCount;
    private TextView mPercentage;
    private CircleImageView mTermImage;
    private TextView mTermImageText;

    public ClassCard(Class c, Activity activity, int innerLayout) {
        super(activity, innerLayout);
        mActivity = activity;
        mClass = c;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        // setup views
        mUnitCount = (TextView) parent.findViewById(R.id.unit_count);
        mPercentage = (TextView) parent.findViewById(R.id.percentage);
        mTermImage = (CircleImageView) parent.findViewById(R.id.card_image);
        Rect rect = new Rect(0, 0, 75, 75);

        // setup color for image
        Bitmap image = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setColor(mClass.getBackgroundColor());
        canvas.drawRect(rect, paint);
        BitmapDrawable b = new BitmapDrawable(image);
        mTermImage.setImageDrawable(b);
        mTermImageText = (TextView) parent.findViewById(R.id.term_image_text);

        //setup text
        mUnitCount.setText(constructTotalUnitsHeader());
        mPercentage.setText(mClass.getCardDisplayText());
        String className = mClass.getClassName();
        mTermImageText.setText(constructImageText(className)); // first letter for text
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

    private String constructImageText(String className) {
        String total = "";
        for (int i = 0; i < className.length(); i++) {
            if (Character.isDigit(className.charAt(i))) {
                total += className.charAt(i);
            }
        }
        if (total.isEmpty()) {
            total = className.substring(0, 3);
        }
        return total;
    }

    public Class getCorrespondingClass() {
        return mClass;
    }



}
