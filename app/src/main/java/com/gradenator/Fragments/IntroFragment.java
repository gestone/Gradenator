package com.gradenator.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gradenator.R;
import com.gradenator.Utilities.Util;

public class IntroFragment extends Fragment {

    public static final String TAG = IntroFragment.class.getSimpleName();

    private Button mAddNewTerm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_frag, container, false);
        findAndSetViews(v);
        return v;
    }

    private void findAndSetViews(View v) {
        mAddNewTerm = (Button) v.findViewById(R.id.add_new_term);
        mAddNewTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.displayFragment(new ViewTermsFragment(), ViewTermsFragment.TAG,
                        getActivity());
            }
        });
    }

}
