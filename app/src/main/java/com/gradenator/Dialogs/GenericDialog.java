package com.gradenator.Dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.gradenator.Utilities.Util;

/**
 * Created by Justin on 9/16/2014.
 */
public class GenericDialog extends DialogFragment {

    public static GenericDialog newInstance(String title, String message) {
        GenericDialog gen = new GenericDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        gen.setArguments(args);
        return gen;
    }

    public AlertDialog show(Activity a) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(a);
        mBuilder.setTitle(getArguments().getString("title"));
        mBuilder.setMessage(getArguments().getString("message"));
        mBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return mBuilder.show();
    }
}
