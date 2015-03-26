package com.sellcom.tracker_interno;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;
import location.GPSTracker;
import util.Utilities;


public class FragmentCustomerWorkPlan extends Fragment implements UIResponseListenerInterface{

    final static public String TAG = "TAG_FRAGMENT_CUSTOMER_WORK_PLAN";
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private Fragment fragmentMap;
    private boolean flag = false;
    private boolean process = false;

    public Button workPlan_Iniciar,btn_continue;

    private TextView txt_pdv_name,
            txt_pdv_status,
            txt_pdv_address,
            txt_initial_scheduled_date,
            txt_scheduled_end_date;
    private float       latitude, longitude;
    private String      id_pdv;
    private String      id_visit;
    private String      status_visit;
    private JSONObject jsonInfo;
    private Utilities utilities;
    private Map<String,String>  data;

    static Context context;
    View view;

    public FragmentCustomerWorkPlan(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_customer_work_plan, container, false);
        Log.d("WorkPlan", "Generando View de Customer Workplan.....");

        data = new HashMap<String, String>();

        utilities = new Utilities(getActivity());

        btn_continue = (Button)view.findViewById(R.id.btn_continue);
        workPlan_Iniciar      = (Button) view.findViewById(R.id.workP_buttonIniciar);
        txt_pdv_name            = (TextView)view.findViewById(R.id.txt_pdv_name);
        txt_pdv_address         = (TextView)view.findViewById(R.id.txt_pdv_address);
        txt_pdv_status    = (TextView)view.findViewById(R.id.txt_pdv_status);
        txt_initial_scheduled_date           = (TextView)view.findViewById(R.id.txt_initial_scheduled_date);
        txt_scheduled_end_date           = (TextView)view.findViewById(R.id.txt_scheduled_end_date);

        //Consuming the detail from WS

        Log.d(TAG,getArguments().getString("response"));

        process = getArguments().getBoolean("process");

        if(process){
            btn_continue.setVisibility(View.VISIBLE);
            workPlan_Iniciar.setVisibility(View.GONE);
        }else{
            workPlan_Iniciar.setVisibility(View.VISIBLE);
            btn_continue.setVisibility(View.GONE);
        }

        boolean fromWS = true;
        if (fromWS){
            try {
                JSONObject  jsonResponse = new JSONObject(getArguments().getString("response"));
                JSONArray aux_array    = jsonResponse.getJSONArray("info_visit");
                jsonInfo                 = aux_array.getJSONObject(0);
                jsonInfo.put("id_visit", jsonResponse.getString("id_visit"));

                txt_pdv_name.setText(jsonInfo.getString("pdv_name"));
                String address = jsonInfo.getString("ad_street")+ ", #"+jsonInfo.getString("ad_ext_num") + ", " + jsonInfo.getString("ad_locality")+
                        ", "+ jsonInfo.getString("ad_city") + ", " + jsonInfo.getString("st_state") + ", " + jsonInfo.getString("cnt_country");
                txt_pdv_address.setText(address);
                txt_pdv_status.setText(utilities.getTypeStatus(jsonInfo.getString("id_visit_status")));
                txt_initial_scheduled_date.setText(utilities.getFormatTime(jsonInfo.getString("vi_schedule_start")));
                txt_scheduled_end_date.setText(utilities.getFormatTime(jsonInfo.getString("vi_schedule_end")));



                id_visit            = jsonResponse.getString("id_visit");
                if(jsonInfo.getString("ad_latitude").equals("null") || jsonInfo.getString("ad_longitude").equals("null")){
                    latitude = 0;
                    longitude = 0;
                }else{
                    latitude            = Float.parseFloat(jsonInfo.getString("ad_latitude"));
                    longitude           = Float.parseFloat(jsonInfo.getString("ad_longitude"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showErrorDialog(getActivity().getString(R.string.req_man_error_contacting_service), this.getActivity());
            }
        }
        else{

        }



        //updateInitButton();

        GPSTracker tracker = new GPSTracker(getActivity());
        final double user_latitude      = tracker.getLatitude();
        final double user_longitude     = tracker.getLongitude();


        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentMap = fragmentManager.findFragmentById(R.id.fragment_map_container);

        if(fragmentMap != null && fragmentMap.isAdded()) {

            SupportMapFragment supportmapfragment = (SupportMapFragment) fragmentMap;
            GoogleMap supportMap = supportmapfragment.getMap();
            //Agregando opciones a supportMap
            if (supportMap != null) {

                supportMap.getUiSettings().setCompassEnabled(true);
                supportMap.getUiSettings().setZoomGesturesEnabled(true);

                //System.out.println("Generado add para mapa: " + add);
                //Generando la ubicación mediante la dirección del cliente...

                Log.v("LATITUDE"," - - -" +latitude);
                Log.v("LONGITUDE"," - - -" +longitude);
                supportMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                        .title(txt_pdv_name.getText().toString())
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_map)));

                supportMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user_latitude, user_longitude), 11));
                supportMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                supportMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


            }
        }

        //Button para iniciar visita
        workPlan_Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location myLocation = new GPSTracker(getActivity()).getCurrentLocation();
                boolean flag = false;

                try{
                    data.put("hil_x",""+myLocation.getLatitude());
                    data.put("hil_y",""+myLocation.getLongitude());
                    flag = true;
                }catch (Exception e){
                    Toast.makeText(getActivity(), getActivity().getString(R.string.activate_gps_service), Toast.LENGTH_SHORT).show();
                    flag = false;
                    return;
                }
                if(flag){
                    prepareRequest(METHOD.START_VISIT,data);
                }



            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id_visit",id_visit);

                fragment        = new FragmentStepVisit();
                fragment.setArguments(bundle);


                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container, fragment, FragmentStepVisit.TAG);
                fragmentTransaction.commit();
                ((MainActivity) getActivity()).depthCounter = 3;
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentManager.beginTransaction().remove(fragmentMap).commit();
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

            if(resp.getString("method").equalsIgnoreCase(METHOD.START_VISIT.toString())){

                if(resp.getString("success").equalsIgnoreCase("true")){

                    Bundle bundle = new Bundle();
                    bundle.putString("id_visit",id_visit);

                    fragment        = new FragmentStepVisit();
                    fragment.setArguments(bundle);


                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container, fragment, FragmentStepVisit.TAG);
                    fragmentTransaction.commit();
                    ((MainActivity) getActivity()).depthCounter = 3;

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onBackWorkPlan(){
        getActivity().onBackPressed();
    }
}
