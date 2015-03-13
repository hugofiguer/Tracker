package com.sellcom.tracker_interno;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;
import util.Utilities;


public class FragmentDialogVisitActivities extends DialogFragment implements View.OnClickListener, UIResponseListenerInterface{


    final static public String TAG = "TAG_FRAGMENT_DIALOG_VISIT_ACTIVITIES";
    private TimePicker pickerTimeStart , pickerTimeEnd;
    private Button btnEndVisit, btnCancelVisit;
    private TextView txt_act_name;
    Calendar cal_now = Calendar.getInstance();
    private Context context;
    private String id_activity, act_name, id_visit, id_evidence_type,foto, totalSeconds;
    private int time_start,minute_start,time_end,minute_end,timeReal,minuteReal;
    activitySuccess activitySuccess;
    private boolean flag = false;

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
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id_activity = getArguments().getString("id_activity");
        act_name = getArguments().getString("act_name");
        id_visit = getArguments().getString("id_visit");
        id_evidence_type = getArguments().getString("id_evidence_type");
        foto = getArguments().getString("foto");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_visit_activities, container, false);

        context = getActivity();
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setTitle(getString(R.string.finalize_activity));

        time_start = cal_now.get(Calendar.HOUR_OF_DAY);
        minute_start = cal_now.get(Calendar.MINUTE);
        time_end = cal_now.get(Calendar.HOUR_OF_DAY);
        minute_end = cal_now.get(Calendar.MINUTE);

        txt_act_name = (TextView)view.findViewById(R.id.txt_act_name);
        txt_act_name.setTextColor(getResources().getColor(R.color.light_green));
        txt_act_name.setText(act_name);

        pickerTimeStart = (TimePicker)view.findViewById(R.id.timePickerStart);
        pickerTimeStart.setCurrentHour(cal_now.get(Calendar.HOUR_OF_DAY));
        pickerTimeStart.setCurrentMinute(cal_now.get(Calendar.MINUTE));
        pickerTimeStart.setIs24HourView(true);
        pickerTimeStart.setOnTimeChangedListener(new OnTimeChangedListener(){

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                    time_start = hourOfDay;
                    minute_start = minute;

            }});

        pickerTimeEnd = (TimePicker)view.findViewById(R.id.timePickerEnd);
        pickerTimeEnd.setCurrentHour(cal_now.get(Calendar.HOUR_OF_DAY));
        pickerTimeEnd.setCurrentMinute(cal_now.get(Calendar.MINUTE));
        pickerTimeEnd.setIs24HourView(true);
        pickerTimeEnd.setOnTimeChangedListener(new OnTimeChangedListener(){

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                time_end = hourOfDay;
                minute_end = minute;

            }});

        btnEndVisit = (Button)view.findViewById(R.id.btnEndVisit);
        btnEndVisit.setOnClickListener(this);

        btnCancelVisit = (Button)view.findViewById(R.id.btnCancelVisit);
        btnCancelVisit.setOnClickListener(this);

        if(time_start > 12){
            time_start = time_start - 12;
        }

        if(time_end > 12){
            time_end = time_end - 12;
        }

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEndVisit:

                Log.e("HORA INICIAL",""+time_start);
                Log.e("HORA FINAL",""+time_end);

                    if(time_start == time_end && minute_start == minute_end){
                        flag = false;
                        Toast.makeText(context,getString(R.string.message_dialog_time),Toast.LENGTH_SHORT).show();
                    }else if(time_start > time_end){
                        flag = false;
                        Toast.makeText(context,getString(R.string.message_dialog_time),Toast.LENGTH_SHORT).show();
                    }else if (time_start == time_end && minute_start > minute_end){
                        flag = false;
                        Toast.makeText(context,getString(R.string.message_dialog_time),Toast.LENGTH_SHORT).show();
                    }else {
                        timeReal = time_end - time_start;

                        if(minute_start > minute_end){
                            minuteReal = minute_start - minute_end;
                            minuteReal = 60 - minuteReal;
                        }else{
                            minuteReal = minute_end - minute_start;
                        }

                        if(time_end > time_start && minute_end < minute_start){
                            if(time_end == time_start+1) {
                                timeReal = 0;

                            }
                        }
                        flag = true;

                    }



                if(flag){
                    totalSeconds ="" + ((minuteReal * 60) + ((timeReal * 60)*60)); //Convertir horas y minutos a segundos para mandarlos a central
                    prepareRequest(METHOD.SEND_EVIDENCE,new HashMap<String, String>());
                }else{
                    Toast.makeText(context,getString(R.string.message_dialog_time),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnCancelVisit:
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {
        /**** Request manager stub
         * 0. Recover data from UI
         * 1. Add credentials information
         * 2. Set the RequestManager listener to 'this'
         * 3. Send the request (Via RequestManager)
         * 4. Wait for it
         */

        // 1
        String token      = Session.getSessionActive(getActivity()).getToken();
        String username   = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);
        params.put("id_activity",id_activity);
        params.put("id_visit",id_visit);
        if(method.equals(METHOD.END_ACTIVITY)){
            params.put("time",totalSeconds);
        }else if(method.equals(METHOD.SEND_EVIDENCE)){
            params.put("id_evidence_type",id_evidence_type);
            params.put("foto",foto);
        }


        //2
        RequestManager.sharedInstance().setListener(this);

        //3
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {

        JSONObject resp;

        try {
            resp        = new JSONObject(stringResponse);
            if (resp.getString("method").equalsIgnoreCase(METHOD.END_ACTIVITY.toString())){
                if (resp.getString("success").equalsIgnoreCase("true")){

                    activitySuccess.getActivitySuccess(true);
                    getDialog().dismiss();



                }
            }else if(resp.getString("method").equalsIgnoreCase(METHOD.SEND_EVIDENCE.toString())){

                if (resp.getString("success").equalsIgnoreCase("true")){


                    prepareRequest(METHOD.END_ACTIVITY,new HashMap<String, String>());


                }


            }



        } catch (JSONException e) {
            e.printStackTrace();
            Utilities.showErrorDialog(getActivity().getString(R.string.req_man_error_contacting_service), this.getActivity());
        }

    }

    /*
    *
    * Interfaz creada para comunicacion con FragmentPadre
     */
    public interface activitySuccess{
        public void getActivitySuccess(boolean success);
    }

    public void setActivitySuccessListener(activitySuccess listener){
        activitySuccess = listener;
    }
}
