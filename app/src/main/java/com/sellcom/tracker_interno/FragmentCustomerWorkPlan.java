package com.sellcom.tracker_interno;


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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import location.GPSTracker;
import util.Utilities;


public class FragmentCustomerWorkPlan extends Fragment {

    final static public String TAG = "TAG_FRAGMENT_CUSTOMER_WORK_PLAN";
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private Fragment fragmentMap;
    private boolean flag = false;

    public Button workPlan_Iniciar;

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

        utilities = new Utilities(getActivity());

        txt_pdv_name            = (TextView)view.findViewById(R.id.txt_pdv_name);
        txt_pdv_address         = (TextView)view.findViewById(R.id.txt_pdv_address);
        txt_pdv_status    = (TextView)view.findViewById(R.id.txt_pdv_status);
        txt_initial_scheduled_date           = (TextView)view.findViewById(R.id.txt_initial_scheduled_date);
        txt_scheduled_end_date           = (TextView)view.findViewById(R.id.txt_scheduled_end_date);

        //Consuming the detail from WS

        Log.d(TAG,getArguments().getString("response"));

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

        workPlan_Iniciar      = (Button) view.findViewById(R.id.workP_buttonIniciar);

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




}
