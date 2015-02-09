package com.sellcom.tracker;


import android.app.Activity;
import android.content.Context;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;
import location.GPSTracker;
import util.DatesHelper;
import util.TrackerManager;


public class FragmentCustomerWorkPlan extends Fragment implements UIResponseListenerInterface {

    final static public String TAG = "customer_work_plan";
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private Fragment fragmentMap;
    private String add;

    public Button workPlan_Iniciar,
            getWorkPlan_Reasignar;

    private TextView txt_pdv_name,
            txt_pdv_status,
            txt_pdv_address,
            txt_pdv_phone_number;
    private float       latitude, longitude;
    private String      pdv_id;
    private String      visit_id;
    private String      visit_status_code;
    private String      real_end;
    private JSONObject jsonInfo;

    static Context context;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_customer_work_plan, container, false);
        Log.d("WorkPlan", "Generando View de Customer Workplan.....");

        txt_pdv_name            = (TextView)view.findViewById(R.id.txt_pdv_name);
        txt_pdv_address         = (TextView)view.findViewById(R.id.txt_pdv_address);
        txt_pdv_phone_number    = (TextView)view.findViewById(R.id.txt_pdv_phone_number);
        txt_pdv_status           = (TextView)view.findViewById(R.id.txt_pdv_status);

        //Consuming the detail from WS

        Log.d(TAG,getArguments().getString("response"));

        boolean fromWS = true;
        if (fromWS){
            try {
                JSONObject  jsonResponse = new JSONObject(getArguments().getString("response"));
                JSONArray aux_array    = jsonResponse.getJSONArray("pdv_info");
                jsonInfo                 = aux_array.getJSONObject(0);
                jsonInfo.put("id_visit",jsonResponse.getString("id_visit"));

                txt_pdv_name.setText(jsonInfo.getString("pdv_name"));
                txt_pdv_address.setText(jsonInfo.getString("pdv_address"));
                txt_pdv_phone_number.setText(jsonInfo.getString("pdv_phone_number"));

                Map<String,String> pdv_active = new HashMap<String, String>();
                pdv_active.put("pdv_name",jsonInfo.getString("pdv_name"));
                pdv_active.put("pdv_id",jsonInfo.getString("pdv_id"));
                pdv_active.put("visit_id",jsonResponse.getString("visit_id"));

                TrackerManager.sharedInstance().setCurrent_pdv(pdv_active);

                pdv_id              = jsonInfo.getString("pdv_id");
                visit_id            = jsonResponse.getString("visit_id");
                latitude            = Float.parseFloat(jsonInfo.getString("pdv_latitude"));
                longitude           = Float.parseFloat(jsonInfo.getString("pdv_longitude"));

                visit_status_code   = jsonResponse.getString("visit_status_id");
                real_end            = jsonResponse.getString("real_end");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{

        }

        workPlan_Iniciar      = (Button) view.findViewById(R.id.workP_buttonIniciar);
        getWorkPlan_Reasignar = (Button) view.findViewById(R.id.workP_buttonReagendar);

        updateInitButton();

        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentMap = fragmentManager.findFragmentById(R.id.fragment_map_container);

        if(fragmentMap != null && fragmentMap.isAdded()) {

            SupportMapFragment supportmapfragment = (SupportMapFragment) fragmentMap;
            GoogleMap supportMap = supportmapfragment.getMap();
            //Agregando opciones a supportMap
            if (supportMap != null) {

                supportMap.getUiSettings().setCompassEnabled(true);
                supportMap.getUiSettings().setZoomGesturesEnabled(true);

                System.out.println("Generado add para mapa: " + add);
                //Generando la ubicación mediante la dirección del cliente...
                supportMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                        .title(txt_pdv_name.getText().toString())
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_map)));

                supportMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                supportMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                supportMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        }

        //Button para iniciar visita
        workPlan_Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (visit_status_code.equalsIgnoreCase("1")){
                    GPSTracker tracker          = new GPSTracker(getActivity());
                    float latitude              = (float)tracker.getLatitude();
                    float longitude             = (float)tracker.getLongitude();

                    Map<String, String> params  = new HashMap<String, String>();
                    params.put("latitude",String.valueOf(latitude));
                    params.put("longitude",String.valueOf(longitude));
                    params.put("date_time", DatesHelper.sharedInstance().getStringDate(new Date()));
                    params.put("visit_id",visit_id);
                    params.put("pdv_id",pdv_id);

                    // Send start date time info to server
                    prepareRequest(METHOD.SEND_START_VISIT, params);
                }
                else if (visit_status_code.equalsIgnoreCase("2")){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("visit_id",visit_id);
                        obj.put("pdv_id",pdv_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    decodeResponse(obj.toString());
                }
                else if (visit_status_code.equalsIgnoreCase("3")){
                    RequestManager.sharedInstance().showErrorDialog("error_cerrada ("+visit_id+") real ("+real_end+")",getActivity());
                }

            }
        });

        //Button para reasignar visita
        //Dejamos el button en GONE, ya que por el momento no lo utilizaremos
        getWorkPlan_Reasignar.setVisibility(View.GONE);
        getWorkPlan_Reasignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "REASIGNAR", Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }

    public void updateInitButton(){
        if (visit_status_code.equalsIgnoreCase("1"))   // AGENDADA (no iniciada)
            workPlan_Iniciar.setText(getActivity().getString(R.string.wp_start));

        else if (visit_status_code.equalsIgnoreCase("2"))    // INICIADA
            workPlan_Iniciar.setText(getActivity().getString(R.string.wp_continue));

        else if (visit_status_code.equalsIgnoreCase("3"))    // FINALIZADA
            workPlan_Iniciar.setText(getActivity().getString(R.string.wp_closed));
    }

    public void onStop(){
        super.onStop();
        fragmentManager.beginTransaction().remove(fragmentMap).commit();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        //2
        RequestManager.sharedInstance().setListener(this);

        //3
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {
        /*
        try {
            TrackerManager.sharedInstance().setCurrent_pdv(RequestManager.sharedInstance().jsonToMap(jsonInfo));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fragment        = new FragmentStepVisit();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.container, fragment, FragmentStepVisit.TAG);
        fragmentTransaction.commit();
        ((MainActivity) getActivity()).depthCounter = 3;*/
    }
}
