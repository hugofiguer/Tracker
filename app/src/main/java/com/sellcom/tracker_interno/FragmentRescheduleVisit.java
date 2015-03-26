package com.sellcom.tracker_interno;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

    private int mYear, mMonth, mDay,mYear_final,mMonth_final,mDay_final;
    private TextView fecha,fecha_final,hora,hora_final;
    private ImageButton imageCalendar,imageCalendar_final,imageClock,imageClock_final;
    private Button btnAccept, btnCancel;
    private Context context;
    final Calendar c = Calendar.getInstance();
    private int hour, minutes,hour_final,minutes_final;
    private String id_visit,nuevaFecha,nuevaHora,nuevaHoraFinal,currentDate;
    private long output,output2;
    private boolean flag = false;
    private Map<String,String> data;
    private Fragment fragment;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rescheduled_visit, container, false);

        data = new HashMap<String, String>();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);

        mYear_final = c.get(Calendar.YEAR);
        mMonth_final = c.get(Calendar.MONTH);
        mDay_final = c.get(Calendar.DAY_OF_MONTH);
        hour_final = c.get(Calendar.HOUR_OF_DAY);
        minutes_final = c.get(Calendar.MINUTE);

        currentDate = "" + mDay + (mMonth + 1) + mYear;

        id_visit = getArguments().getString("id_visit");

        context = getActivity();
        fecha = (TextView)view.findViewById(R.id.txt_fecha);
        fecha.setTextColor(getResources().getColor(R.color.white));
        fecha.setClickable(true);


        // Asignar fecha actual a textview en formato dd/mm/yyyy
        nuevaFecha = getNewDate(mDay,mMonth,mYear);
        fecha.setText(""+nuevaFecha);

        fecha.setOnClickListener(this);
        imageCalendar = (ImageButton)view.findViewById(R.id.imageCalendar);
        imageCalendar.setOnClickListener(this);


        hora = (TextView)view.findViewById(R.id.txt_hora);
        hora.setTextColor(getResources().getColor(R.color.white));
        hora.setClickable(true);

        hora_final = (TextView)view.findViewById(R.id.txt_hora_final);
        hora_final.setTextColor(getResources().getColor(R.color.white));
        hora_final.setClickable(true);

        // Asignar hora actual a textview en formato hh:mm
        nuevaHora = getNewTime(hour,minutes);
        hora.setText(""+nuevaHora);

        nuevaHoraFinal = getNewTimeEnd(hour_final,minutes_final);
        hora_final.setText(""+nuevaHoraFinal);

        hora.setOnClickListener(this);
        hora_final.setOnClickListener(this);
        imageClock = (ImageButton)view.findViewById(R.id.imageHora);
        imageClock.setOnClickListener(this);

        imageClock_final = (ImageButton)view.findViewById(R.id.imageHora_final);
        imageClock_final.setOnClickListener(this);

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
                                hour = hourOfDay;
                                minutes = minute;
                            }
                        }, hour,minutes,true);
                tpd.setCancelable(false);
                tpd.setIcon(getResources().getDrawable(R.drawable.icon_reloj));
                tpd.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tpd.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                tpd.show();


                break;
            case R.id.txt_hora_final:
            case R.id.imageHora_final:

                // Lanzar Time Picker Dialog
                TimePickerDialog tpd_final = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Setear valor en editText

                                nuevaHoraFinal = getNewTimeEnd(hourOfDay,minute);
                                hora_final.setText(""+nuevaHoraFinal);
                                hora_final.setClickable(true);
                                imageClock.setClickable(true);
                                hour_final = hourOfDay;
                                minutes_final = minute;
                            }
                        }, hour_final,minutes_final,true);
                tpd_final.setCancelable(false);
                tpd_final.setIcon(getResources().getDrawable(R.drawable.icon_reloj));
                tpd_final.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tpd_final.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                tpd_final.show();

                break;
            case R.id.btnAccept:
                //btnAccept.setClickable(false);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
                try {
                    boolean flag2 = true;
                    //Para comprobar que la fecha elegida no pertenesca al pasado
                    if(Integer.parseInt(nuevaFecha.substring(6,10))>mYear){
                        flag2 = false;
                    }else if(Integer.parseInt(nuevaFecha.substring(6,10))==mYear && Integer.parseInt(nuevaFecha.substring(3,5))>(mMonth+1)){
                        flag2 = false;
                    }else if(Integer.parseInt(nuevaFecha.substring(6,10))==mYear && Integer.parseInt(nuevaFecha.substring(3,5))==(mMonth+1) &&
                            Integer.parseInt(nuevaFecha.substring(0,2))>mDay){
                        flag2 = false;
                    }else{
                        flag2 = true;
                    }

                    //La fecha elegida ya paso, favor de verificar la nueva fecha
                    if(flag2){
                        Toast.makeText(context,getString(R.string.message_reschedule_visit),Toast.LENGTH_LONG).show();

                    }else //La fecha elegida aun no pasa, fecha correcta
                    {

                        if(hour == hour_final && minutes == minutes_final){
                            flag = false;
                            Toast.makeText(context,getString(R.string.message_dialog_time),Toast.LENGTH_SHORT).show();
                        }else if(hour > hour_final){
                            flag = false;
                            Toast.makeText(context,getString(R.string.message_dialog_time),Toast.LENGTH_SHORT).show();
                        }else if (hour == hour_final && minutes > minutes_final){
                            flag = false;
                            Toast.makeText(context,getString(R.string.message_dialog_time),Toast.LENGTH_SHORT).show();
                        }else {
                            flag = true;

                        }

                        if(flag){
                            Date date = sdf.parse(nuevaFecha+","+nuevaHora);
                            output = Long.valueOf(date.getTime()/1000);

                            Date dateFinal = sdf.parse(nuevaFecha+","+nuevaHoraFinal);
                            output2 = Long.valueOf(dateFinal.getTime()/1000);

                            data.put("vi_schedule_start",""+output);
                            data.put("vi_schedule_end",""+output2);

                            prepareRequest(METHOD.SEND_RESCHEDULED_VISIT, data);
                        }else{
                            Toast.makeText(context,getString(R.string.message_dialog_time),Toast.LENGTH_SHORT).show();
                        }




                    }



//Parse
                } catch (Exception e) {
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

    public String getNewTimeEnd(int hour, int minutes){

        String newTime;

        if(hour<10 && minutes <10) {
            newTime = "0" + hour + ":0" + minutes;
            hora_final.setText(nuevaHoraFinal);
        }else if (minutes<10){
            newTime = hour + ":0" + minutes;
            hora_final.setText(nuevaHoraFinal);
        }else if(hour<10){
            newTime = "0" +hour + ":" + minutes;
            hora_final.setText(nuevaHoraFinal);
        }else{
            newTime = hour + ":" + minutes;
            hora_final.setText(nuevaHoraFinal);
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

                if(resp.getString("success").equalsIgnoreCase("true")){
                    Toast.makeText(context,getString(R.string.rescheduled_visit),Toast.LENGTH_LONG).show();

                    fragment = new FragmentWorkPlan();
                    //getActivity().getSupportFragmentManager().popBackStack();
                    //getActivity().getSupportFragmentManager().popBackStack();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                    fragmentTransaction.replace(R.id.container, fragment, FragmentWorkPlan.TAG);
                    fragmentTransaction.commit();
                    ((MainActivity) getActivity()).depthCounter = 1;
                }else {
                    //No se pudo reagendar la visita
                }



            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
