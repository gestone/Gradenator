package com.gradenator.Background;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.gradenator.Internal.DataPoint;
import com.gradenator.Internal.Session;
import com.gradenator.Internal.Term;
import com.gradenator.Internal.Class;
import com.gradenator.R;

import java.io.File;
import java.util.List;

/**
 * Used to update and store grades on a weekly basis for graphing purposes.
 */
public class GradeUpdateReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context c, Intent i) {
        updateGradePercentages(c);

    }

    private void updateGradePercentages(Context c) {
        // if the application is not active...
        if (!noFilesContained(c)) {
            Term curTerm = Session.getInstance(c).getAllTerms().get(0);
            List<Class> allClasses = curTerm.getAllClasses();
            boolean updated = false;
            for (Class singleClass : allClasses) { // grab percentage and record it
                if (singleClass.getAllAssignments().size() > 0) { // need to make sure it's not N/A
                    List<DataPoint> dataPoints = singleClass.getDataPoints();
                    dataPoints.add(new DataPoint(singleClass.getCurrentPercentage(),
                            System.currentTimeMillis()));
                    if(dataPoints.size() > 6) {
                        dataPoints = dataPoints.subList(1, dataPoints.size());
                        // only keep a subset of 6 elements
                    }
                    updated = true;
                }
            }
            if (updated) {
                Session.getInstance(c).saveTerms(c); // save to JSON file
                Resources r = c.getResources();
                NotificationManager notifyGradesUpdated = (NotificationManager) c
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Notification gradeUpdate = new Notification.Builder(c).setContentTitle(r
                        .getString(R.string.grade_update_title))
                        .setContentText(r.getString(R.string.grade_update_text))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(null)
                        .setAutoCancel(true)
                        .build();
                notifyGradesUpdated.notify(0, gradeUpdate);
            }
        }
    }

    private boolean noFilesContained(Context c) {
       File[] allFiles = c.getFilesDir().listFiles();
       return allFiles.length == 0;
    }

}
