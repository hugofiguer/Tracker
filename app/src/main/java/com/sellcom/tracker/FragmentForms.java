package com.sellcom.tracker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;
import util.FormAdapter;
import util.ListViewItem;

public class FragmentForms extends Fragment implements UIResponseListenerInterface{

    final static public String TAG = "TAG_FRAGMENT_FORM";
    private ArrayList<View>     mViews;
    private LayoutInflater      layoutInflater;
    private View                viewBinary,viewText,viewRadio;
    private FormAdapter         formAdapter;
    private ListView            listView;
    private String              id_visit;
    private Context             context;

    public static final int TYPE_BINARY = 0;
    public static final int TYPE_TEXT   = 1;
    public static final int TYPE_RADIO  = 2;


    public FragmentForms() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forms, container, false);

        id_visit = getArguments().getString("id_visit");
        mViews = new ArrayList<View>();
        listView = (ListView) view.findViewById(R.id.listView);


        final ListViewItem[] items = new ListViewItem[20];

        for (int i = 0; i < items.length; i++) {

            if (i == 9 || i == 13) {

                items[i] = new ListViewItem(TYPE_RADIO);
            } else if (i % 2 == 0) {
                items[i] = new ListViewItem(TYPE_TEXT);
            } else {
                items[i] = new ListViewItem(TYPE_BINARY);
            }

            // mViews.add(viewItem);
        }

        formAdapter = new FormAdapter(context,android.R.layout.simple_list_item_1, items);
        listView.setAdapter(formAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity(), "Posicion: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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

        try{
            resp = new JSONObject(stringResponse);
            Log.v("FORMULARIO",""+resp.toString());

        }catch (JSONException e){
            e.printStackTrace();
        }


    }
}
