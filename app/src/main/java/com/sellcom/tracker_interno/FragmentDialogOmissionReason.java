package com.sellcom.tracker_interno;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;
import util.OmissionActivityAdapter;


public class FragmentDialogOmissionReason extends DialogFragment implements View.OnClickListener, UIResponseListenerInterface{


    final static public String          TAG                                 = "TAG_FRAGMENT_DIALOG_OMISSION_REASON";
    Fragment                            fragment;
    private FragmentManager             fragmentManager;

    private Context                     context;
    private ListView                    lv_omission_reason;
    private Button                      btn_send_omission_activities;
    private String                      info_omission_activities;
    private JSONArray                   arrayObjectOmission;
    private JSONObject                  objectOmission;
    private Map<String,String>          data_omission_reason,
                                        data_map;
    private List<Map<String,String>>    omission_reason,
                                        data_list_map;
    private EditText                    edt_omission_reason;
    private String                      id_visit;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        fragmentManager = getActivity().getSupportFragmentManager();
        //PROPIEDADES PARA MI DIALOG
        //setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_MinWidth);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_dialog_omission_reason, container, false);

        data_list_map = new ArrayList<Map<String, String>>();
        lv_omission_reason              = (ListView) view.findViewById(R.id.lv_omission_reason);
        btn_send_omission_activities    = (Button) view.findViewById(R.id.btn_send_omission_activities);
        btn_send_omission_activities.setOnClickListener(this);

        omission_reason             = new ArrayList<Map<String, String>>();
        info_omission_activities    = getArguments().getString("info_omission_activities");
        id_visit                    = getArguments().getString("id_visit");
        try {
        arrayObjectOmission = new JSONArray(info_omission_activities);
            for(int i = 0; i < arrayObjectOmission.length(); i++){
                objectOmission = arrayObjectOmission.getJSONObject(i);

                data_omission_reason = new HashMap<String, String>();

                data_omission_reason.put("act_name",objectOmission.getString("act_name"));
                data_omission_reason.put("id_activity",objectOmission.getString("id_activity"));
                data_omission_reason.put("acv_time",objectOmission.getString("acv_time"));

                if(Integer.parseInt(arrayObjectOmission.getJSONObject(i).getString("acv_time"))==0){
                    omission_reason.add(data_omission_reason);
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        lv_omission_reason.setAdapter(new OmissionActivityAdapter(context, getActivity(), omission_reason));



        return view;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_send_omission_activities:
                dismiss();
                //lv_omission_reason.getChildAt(0).findViewById(R.id.edt_omission_reason);
                for (int i=0; i<omission_reason.size(); i++){

                    data_map = new HashMap<String, String>();

                    edt_omission_reason = (EditText) lv_omission_reason.getChildAt(i).findViewById(R.id.edt_omission_reason);
                    data_map.put("id_activity",omission_reason.get(i).get("id_activity"));
                    data_map.put("reason",edt_omission_reason.getText().toString());

                    data_list_map.add(data_map);

                    Log.d("id_activity",i+" -- "+data_list_map.get(i).get("id_activity"));
                    Log.d("reason",i+" -- "+data_list_map.get(i).get("reason"));
                }

                Toast.makeText(context,"Finalizar",Toast.LENGTH_SHORT).show();

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
        params.put("id_visit",id_visit);


        //2
        RequestManager.sharedInstance().setListener(this);

        //3
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);

    }

    @Override
    public void decodeResponse(String stringResponse) {

    }
}
