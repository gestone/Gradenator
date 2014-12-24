package com.gradenator.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gradenator.R;
import com.gradenator.Utilities.Util;

import at.markushi.ui.CircleButton;

/**
 * The first screen that the user sees upon starting up Gradenator. This Fragment is no longer
 * displayed once the user has added a Term.
 */
public class IntroFragment extends Fragment {

    public static final String TAG = IntroFragment.class.getSimpleName();

    private CircleButton mAddNewTerm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_frag, container, false);
        mAddNewTerm = (CircleButton) v.findViewById(R.id.add_new_term);
        mAddNewTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayFragment(new ViewTermsFragment(), ViewTermsFragment.TAG,
                        getActivity());
            }
        });
        Util.changeActionBarTitle(getActivity(), getString(R.string.app_name));
        return v;
    }
}
