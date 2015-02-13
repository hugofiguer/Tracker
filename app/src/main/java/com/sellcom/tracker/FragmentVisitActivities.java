package com.sellcom.tracker;


import android.content.Context;
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


public class FragmentVisitActivities extends Fragment implements AdapterView.OnItemClickListener, UIResponseListenerInterface{

    final static public String TAG = "TAG_FRAGMENT_VISIT_ACTIVITIES";

    static Context context;
    public Fragment fragment;
    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    List<Map<String,String>> elementsList;
    private ListView listActivities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();


        prepareRequest(METHOD.GET_ACTIVITIES,new HashMap<String, String>());

        View view = inflater.inflate(R.layout.fragment_visit_activities, container, false);
        listActivities = (ListView)view.findViewById(R.id.list_visit_activities);


        listActivities.setOnItemClickListener(this);



        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Toast.makeText(context, elementsList.get(position).get("act_name"), Toast.LENGTH_SHORT).show();


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
        params.put("id_visit",getArguments().getString("id_visit"));
        Log.e("En PREPARERESPONDEVISIT", "-------------------------------------------   " + getArguments().getString("id_visit"));


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

            // Prepare data for ExpandableList
            if (resp.getString("method").equalsIgnoreCase(METHOD.GET_ACTIVITIES.toString())) {
                JSONArray act_array = resp.getJSONArray("info_activities");
                String strArray     = act_array.toString();

                elementsList   = new ArrayList<Map<String, String>>();

                try {
                    JSONArray jsonArray = new JSONArray(strArray);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object          = jsonArray.getJSONObject(i);
                        elementsList.add(RequestManager.sharedInstance().jsonToMap(object));
                    }

                    listActivities.setAdapter(new VisitActivitiesAdapter(context,elementsList));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
