package com.sellcom.tracker_interno;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AdapterView;

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
import util.FormsAdapter;


public class FragmentForms extends Fragment implements UIResponseListenerInterface, AdapterView.OnItemClickListener{

    final static public String              TAG = "TAG_FRAGMENT_FORM";
    private LayoutInflater                  layoutInflater;
    private ListView                        listViewForms;
    private FormsAdapter                    formsAdapter;
    private List<Map<String,String>>        listForms;
    private Map<String,String>              data;

    public Fragment fragment;

    private String              id_visit,nameForm;
    private Context context;


    public FragmentForms() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View vista = inflater.inflate(R.layout.fragment_forms, container, false);

        id_visit = getArguments().getString("id_visit");
        listViewForms = (ListView)vista.findViewById(R.id.listViewForms);
        listViewForms.setOnItemClickListener(this);
        listForms = new ArrayList<Map<String, String>>();



        prepareRequest(METHOD.GET_FORM,new HashMap<String, String>());


        return vista;
    }

    public void OnInit(JSONArray jsonArray){

        JSONArray jsonArray1 = jsonArray;

        try {
                JSONArray array = new JSONArray();
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject object = jsonArray1.getJSONObject(i);

                if(Integer.parseInt(object.getString("df_finish"))==0) {
                    data = new HashMap<String, String>();
                    data.put("id_data_form", object.getString("id_data_form"));
                    data.put("dtf_name", object.getString("dtf_name"));
                    data.put("dtf_obligatory", object.getString("dtf_obligatory"));
                    data.put("questions", object.getString("questions"));

                    listForms.add(data);
                }

            }

            formsAdapter = new FormsAdapter(getActivity(),listForms);
            listViewForms.setAdapter(formsAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


        Bundle bundle = new Bundle();
        bundle.putString("id_visit",id_visit);
        bundle.putString("id_data_form",listForms.get(position).get("id_data_form"));
        bundle.putString("dtf_name",listForms.get(position).get("dtf_name"));
        bundle.putString("dtf_obligatory",listForms.get(position).get("dtf_obligatory"));
        bundle.putString("questions",listForms.get(position).get("questions"));

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment = new FragmentQuestionsForm();
        fragment.setArguments(bundle);


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.container, fragment, FragmentQuestionsForm.TAG);
        fragmentTransaction.commit();

        ((MainActivity) getActivity()).depthCounter = 5;

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

            if (resp.getString("method").equalsIgnoreCase(METHOD.GET_FORM.toString())) {
                if(resp.getString("success").equalsIgnoreCase("true")){
                    JSONArray jsonArray = resp.getJSONArray("get_form");
                    OnInit(jsonArray);
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }


    }


}
