package com.sellcom.tracker;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.AdapterView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import util.VisitActivitiesAdapter;


public class FragmentVisitActivities extends Fragment implements AdapterView.OnItemClickListener{

    final static public String TAG = "TAG_FRAGMENT_VISIT_ACTIVITIES";

    static Context context;
    public Fragment fragment;
    List<Map<String,String>> elementsList;
    private ListView listActivities;
    private String id_visit;
    private int[] status = null;
    //private int position;
    private String jsonObjectActivities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();

        elementsList   = new ArrayList<Map<String, String>>();
        id_visit = getArguments().getString("id_visit");
        jsonObjectActivities = getArguments().getString("jsonObjectActivities");


        View view = inflater.inflate(R.layout.fragment_visit_activities, container, false);
        listActivities = (ListView)view.findViewById(R.id.list_visit_activities);
        listActivities.setOnItemClickListener(this);

        getActivities(jsonObjectActivities);
        view.refreshDrawableState();
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        /*
        this.position = position;

        if(Integer.parseInt(elementsList.get(position).get("acv_time"))==0) {
            Bundle bundle = new Bundle();
            bundle.putString("id_activity", elementsList.get(position).get("id_activity"));
            bundle.putString("act_name",elementsList.get(position).get("act_name"));
            bundle.putString("act_description",elementsList.get(position).get("act_description"));
            bundle.putString("id_visit",id_visit);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentDialogVisitActivities dialogo = new FragmentDialogVisitActivities();
            dialogo.setActivitySuccessListener(this);
            dialogo.setArguments(bundle);
            dialogo.show(fragmentManager, FragmentDialogVisitActivities.TAG);
        }else{
            Toast.makeText(context,getString(R.string.finished_activity),Toast.LENGTH_SHORT).show();
        }*/

    }

    public void getActivities(String jsonObjectActivities){

        JSONObject resp;

        try {
            resp        = new JSONObject(jsonObjectActivities);

            if (resp.getString("method").equalsIgnoreCase(METHOD.GET_ACTIVITIES.toString())) {
                JSONArray act_array = resp.getJSONArray("info_activities");
                String strArray     = act_array.toString();

                try {
                    JSONArray jsonArray = new JSONArray(strArray);
                    status = new int[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object          = jsonArray.getJSONObject(i);
                        elementsList.add(RequestManager.sharedInstance().jsonToMap(object));

                        Log.d("en el array","   - - - - - -- - - "+ elementsList.get(i).get("acv_time"));
                        if(Integer.parseInt(elementsList.get(i).get("acv_time"))>0){
                            status[i] = 1;
                        }else{
                            status[i] = 0;
                        }


                    }

                    listActivities.setAdapter(new VisitActivitiesAdapter(context,elementsList,status));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
