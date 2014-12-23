package com.gradenator;

import android.app.Application;

import com.gradenator.R;

import org.acra.*;
import org.acra.ACRA;
import org.acra.annotation.*;

/**
 * Used for debugging to track errors.
 */
@ReportsCrashes(formKey = "", // will not be used
        mailTo = "justinharjanto@hotmail.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)

public class ErrorReportingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
