package com.sellcom.tracker_interno;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
import util.StepVisitAdapter;
import util.Utilities;


public class FragmentStepVisit extends Fragment implements AdapterView.OnItemClickListener, UIResponseListenerInterface {

    final static public String          TAG                     = "TAG_FRAGMENT_STEP_VISIT";
    private Context                     context;
    private Button                      getWorkPlan_Reasignar,
                                        btn_finish_visit;
    Fragment                            fragment;
    private FragmentManager             fragmentManager;
    private String                      id_visit,
                                        jsonObjectActivities = null;
    private List<Map<String,String>>    elementsList;
    private boolean                     flag = false,
                                        flag_omission = false;

    private JSONArray                   arrayObjectOmission;
    private Bundle                      bundleOmission;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        bundleOmission = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_visit, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();
        elementsList   = new ArrayList<Map<String, String>>();


        id_visit = getArguments().getString("id_visit");

        getWorkPlan_Reasignar = (Button) view.findViewById(R.id.workP_buttonReagendar);
        //Button para reasignar visita
        getWorkPlan_Reasignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id_visit",id_visit);

                fragment = new FragmentRescheduleVisit();
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container, fragment, FragmentRescheduleVisit.TAG);
                fragmentTransaction.commit();
                ((MainActivity) getActivity()).depthCounter = 4;

            }
        });

        btn_finish_visit = (Button)view.findViewById(R.id.btn_finish_visit);


        btn_finish_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    for(int i = 0; i < arrayObjectOmission.length(); i++){


                        /*data_omission_reason = new HashMap<String, String>();

                            data_omission_reason.put("act_name",objectOmission.getString("act_name"));
                            data_omission_reason.put("id_activity",objectOmission.getString("id_activity"));
                            data_omission_reason.put("acv_time",objectOmission.getString("acv_time"));
                        */
                            if(Integer.parseInt(arrayObjectOmission.getJSONObject(i).getString("acv_time"))==0){
                                flag_omission = true;
                                //omission_reason.add(data_omission_reason);
                            }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(flag_omission){
                    bundleOmission.putString("info_omission_activities",arrayObjectOmission.toString());
                    bundleOmission.putString("id_visit",id_visit);

                    FragmentDialogOmissionReason fragmentDialogOmissionReason = new FragmentDialogOmissionReason();
                    fragmentDialogOmissionReason.setArguments(bundleOmission);
                    fragmentDialogOmissionReason.show(fragmentManager,FragmentDialogOmissionReason.TAG);

                }else{
                    //TODAS LAS ACTIVIDADES SE COMPLETARON CORRECTAMENTE
                }


            }
        });

        prepareRequest(METHOD.GET_ACTIVITIES, new HashMap<String, String>());

        if (view != null) {
            OnInit(view);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void OnInit(View view) {
        GridView gridView = (GridView) view.findViewById(R.id.gridViewElements);
        gridView.setAdapter(new StepVisitAdapter(getActivity()));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
        Bundle bundle = new Bundle();
        bundle.putString("id_visit",id_visit);


        if(id_visit.equals("") == false && jsonObjectActivities != null){

            switch(position){
                case 0:

                    if(flag){
                        bundle.putString("jsonObjectActivities",jsonObjectActivities);
                        fragment        = new FragmentEvidence();
                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.container, fragment, FragmentEvidence.TAG);
                        ((MainActivity) getActivity()).depthCounter = 4;
                    }else{
                        //Toast.makeText(context,"Todas las actividades han sido realizadas,ahora puede finalizar la visita",Toast.LENGTH_SHORT).show();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(getString(R.string.success_activities))
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // FIRE ZE MISSILES!
                                        dialog.dismiss();
                                        getActivity().onBackPressed();
                                    }
                                });

                        // Create the AlertDialog object and return it
                        builder.setIcon(R.drawable.ic_action_warning);
                        builder.setCancelable(false);
                        builder.show();
                        ((MainActivity) getActivity()).depthCounter = 4;
                    }


                    break;
                case 1:
                    bundle.putString("jsonObjectActivities",jsonObjectActivities);
                    fragment        = new FragmentVisitActivities();
                    fragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.container, fragment, FragmentVisitActivities.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;

                    break;

                case 2:

                    fragment        = new FragmentForms();
                    fragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.container, fragment, FragmentForms.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;

                    break;
            }

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }else{
            Toast.makeText(getActivity(),"No tiene visitas o formularios asignados, pongase en contacto con el administrador",Toast.LENGTH_LONG).show();
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

        JSONObject resp;
        jsonObjectActivities = stringResponse;

        Log.e("JSONOBJECTACTIVITIES",""+jsonObjectActivities);

        try {
            resp        = new JSONObject(stringResponse);
            if(resp.getString("method").equalsIgnoreCase(METHOD.GET_ACTIVITIES.toString())){

                if(resp.getString("success").equalsIgnoreCase("true")){
                    JSONArray act_array = resp.getJSONArray("info_activities");
                    String strArray     = act_array.toString();

                    try {
                        JSONArray jsonArray = new JSONArray(strArray);
                        arrayObjectOmission = jsonArray;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object          = jsonArray.getJSONObject(i);
                            elementsList.add(RequestManager.sharedInstance().jsonToMap(object));
                            Log.e("acv_time",""+Integer.parseInt(elementsList.get(i).get("acv_time")));
                            if(Integer.parseInt(elementsList.get(i).get("acv_time"))==0){
                                flag = true;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getActivity(), "No hay Actividades", Toast.LENGTH_SHORT).show();
                }


            }else if(resp.getString("method").equalsIgnoreCase(METHOD.OMISSION_ACTIVITY.toString())){

                if(resp.getString("success").equalsIgnoreCase("true")){

                }else{

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
