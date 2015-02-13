package com.sellcom.tracker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.Button;

import java.util.Calendar;


public class FragmentDialogVisitActivities extends DialogFragment{


    final static public String TAG = "TAG_FRAGMENT_DIALOG_VISIT_ACTIVITIES";
    private TimePicker pickerTimeStart , pickerTimeEnd;
    private Button btnEndVisit;
    Calendar cal_now = Calendar.getInstance();
    private Context context;

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

        context = getActivity();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        pickerTimeStart = (TimePicker)view.findViewById(R.id.timePickerStart);
        pickerTimeStart.setCurrentHour(cal_now.get(Calendar.HOUR_OF_DAY));
        pickerTimeStart.setCurrentMinute(cal_now.get(Calendar.MINUTE));
        pickerTimeStart.setIs24HourView(true);
        pickerTimeStart.setOnTimeChangedListener(new OnTimeChangedListener(){

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {



            }});

        pickerTimeEnd = (TimePicker)view.findViewById(R.id.timePickerEnd);
        pickerTimeEnd.setCurrentHour(cal_now.get(Calendar.HOUR_OF_DAY));
        pickerTimeEnd.setCurrentMinute(cal_now.get(Calendar.MINUTE));
        pickerTimeEnd.setIs24HourView(true);
        pickerTimeEnd.setOnTimeChangedListener(new OnTimeChangedListener(){

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {



            }});

        btnEndVisit = (Button)view.findViewById(R.id.btnEndVisit);
        btnEndVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }


}
