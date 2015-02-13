package com.sellcom.tracker;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;
import util.Utilities;


public class FragmentRescheduleVisit extends Fragment implements View.OnClickListener, UIResponseListenerInterface{

    final static public String TAG = "TAG_FRAGMENT_RESCHEDULED_VISIT";

    private int mYear, mMonth, mDay;
    private TextView fecha,hora;
    private ImageButton imageCalendar,imageClock;
    private Button btnAccept, btnCancel;
    private Context context;
    final Calendar c = Calendar.getInstance();
    private int hour, minutes;
    private String id_visit,nuevaFecha,nuevaHora,currentDate;
    private long output;
    private Utilities utilities;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rescheduled_visit, container, false);

       utilities = new Utilities(getActivity());

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);

        currentDate = "" + mDay + (mMonth + 1) + mYear;

        id_visit = getArguments().getString("id_visit");

        context = getActivity();
        fecha = (TextView)view.findViewById(R.id.txt_fecha);
        fecha.setTextColor(getResources().getColor(R.color.black));
        fecha.setClickable(true);

        // Asignar fecha actual a textview en formato dd/mm/yyyy
        nuevaFecha = getNewDate(mDay,mMonth,mYear);
        fecha.setText(""+nuevaFecha);

        fecha.setOnClickListener(this);
        imageCalendar = (ImageButton)view.findViewById(R.id.imageCalendar);
        imageCalendar.setOnClickListener(this);

        hora = (TextView)view.findViewById(R.id.txt_hora);
        hora.setTextColor(getResources().getColor(R.color.black));
        hora.setClickable(true);

        // Asignar hora actual a textview en formato hh:mm
        nuevaHora = getNewTime(hour,minutes);
        hora.setText(""+nuevaHora);

        hora.setOnClickListener(this);
        imageClock = (ImageButton)view.findViewById(R.id.imageHora);
        imageClock.setOnClickListener(this);

        btnAccept = (Button)view.findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);

        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);



        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_fecha:
            case R.id.imageCalendar:
                fecha.setClickable(false);
                imageCalendar.setClickable(false);
                // Process to get Current Date

                // Lanzar Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Setear valor en editText

                                nuevaFecha = getNewDate(dayOfMonth,monthOfYear,year);
                                fecha.setText(""+nuevaFecha);
                                fecha.setClickable(true);
                                imageCalendar.setClickable(true);

                            }
                        }, mYear, mMonth, mDay);
                dpd.setIcon(getResources().getDrawable(R.drawable.icon_calendary));
                dpd.setCancelable(false);
                dpd.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dpd.setMessage(getString(R.string.message_reschedule_visit));
                dpd.show();

                break;

            case R.id.txt_hora:
            case R.id.imageHora:
                hora.setClickable(false);
                imageClock.setClickable(false);

                // Lanzar Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Setear valor en editText

                                nuevaHora = getNewTime(hourOfDay,minute);
                                hora.setText(""+nuevaHora);
                                hora.setClickable(true);
                                imageClock.setClickable(true);
                            }
                        }, hour,minutes,true);
                tpd.setCancelable(false);
                tpd.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tpd.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                tpd.show();


                break;
            case R.id.btnAccept:
                btnAccept.setClickable(false);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
                try {
                    boolean flag = false;
                    //Para comprobar que la fecha elegida no pertenesca al pasado
                    if(mYear>Integer.parseInt(nuevaFecha.substring(6,10))){
                        flag = true;
                    }else if(mYear<Integer.parseInt(nuevaFecha.substring(6,10))){
                        flag = false;
                    }else if(mYear==Integer.parseInt(nuevaFecha.substring(6,10)) && mMonth>=Integer.parseInt(nuevaFecha.substring(3,5))
                            && mDay>=Integer.parseInt(nuevaFecha.substring(0,2))){
                        flag = true;
                    }else if(mDay>Integer.parseInt(nuevaFecha.substring(0,2))){
                        flag = true;
                    }

                    //La fecha elegida ya paso, favor de verificar la nueva fecha
                    if(flag){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setMessage("\n\n"+getString(R.string.message_reschedule_visit)+"\n\n")
                                    .setIcon(getResources().getDrawable(R.drawable.ic_action_warning))
                                    .setTitle(getString(R.string.title_dialog_message))
                                    .setPositiveButton(getString(R.string.accept),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    btnAccept.setClickable(true);
                                                }
                                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                    }else //La fecha elegida aun no pasa, fecha correcta
                    {
                        Date date = sdf.parse(nuevaFecha+","+nuevaHora);
                        output = Long.valueOf(date.getTime()/1000);
                        prepareRequest(METHOD.SEND_RESCHEDULED_VISIT, new HashMap<String,String>());
                        Log.d("PARSE DATE", "" + output);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btnCancel:

                getActivity().onBackPressed();

                break;
        }
    }

    public String getNewTime(int hour, int minutes){

        String newTime;

        if(hour<10 && minutes <10) {
            newTime = "0" + hour + ":0" + minutes;
            hora.setText(nuevaHora);
        }else if (minutes<10){
            newTime = hour + ":0" + minutes;
            hora.setText(nuevaHora);
        }else if(hour<10){
            newTime = "0" +hour + ":" + minutes;
            hora.setText(nuevaHora);
        }else{
            newTime = hour + ":" + minutes;
            hora.setText(nuevaHora);
        }

        return newTime;

    }

    public String getNewDate(int day, int month, int year){

        String newDate;

        if(day<10 && month <10) {
            newDate = "0" + day + "/0" + (month + 1) + "/" + year;
            fecha.setText(nuevaFecha);
        }else if (day<10){
            newDate = "0" + day + "/" + (month + 1) + "/" + year;
            fecha.setText(nuevaFecha);
        }else if(month<10){
            newDate = day + "/0" + (month + 1) + "/" + year;
            fecha.setText(nuevaFecha);
        }else{
            newDate = day + "/" + (month + 1) + "/" + year;
            fecha.setText(nuevaFecha);
        }

        return newDate;

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
        params.put("id_visit",id_visit);
        params.put("vi_schedule_start",""+output);

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

            if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_RESCHEDULED_VISIT.toString())){

                Log.d("----", "--------------");

                getActivity().onBackPressed();
                getActivity().onBackPressed();
                Toast.makeText(context,utilities.getFormatDate(resp.getString("new_reschedule_date")),Toast.LENGTH_SHORT).show();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
