package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class FragmentDialogVisitActivities extends DialogFragment{


    final static public String TAG = "TAG_FRAGMENT_DIALOG_VISIT_ACTIVITIES";

    public FragmentDialogVisitActivities() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null) {
            return;
        }
        setCancelable(false);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_visit_activities, container, false);


        return view;
    }


}
