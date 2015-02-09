package com.sellcom.tracker;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;
import util.ParentWorkPlan;


public class FragmentWorkPlan extends Fragment implements UIResponseListenerInterface{

    final static public String TAG = "workplan";
    private int ChildClickStatus=-1;

    static Context context;
    public Fragment fragment;
    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    View view;

    public WorkPlanAdapter                  expandableListAdapter;
    public ExpandableListView expandableListView;
    public ImageView groupExpandlistIconCollapse;
    public ImageView                        grouoExpandlistIconExpand;
    public ParentWorkPlan parent;

    protected List<String> listDataHeader;
    protected HashMap<String, List<Map<String,String>>> listDataChild;

    Map<String,String>                      selectedData;

    String   visit_selected_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_work_plan, container, false);
        Log.d("WorkPlan", "Generando View de Workplan..... y inicializando depthCounter a 1");

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Map<String,String>>>();

        expandableListView = (ExpandableListView) view.findViewById(R.id.expList__workplan);

        groupExpandlistIconCollapse =  (ImageView) view.findViewById(R.id.icon_workplan_expandableCollapse);
        grouoExpandlistIconExpand   =  (ImageView) view.findViewById(R.id.icon_workplan_expandableExpand);

        expandableListView.setIndicatorBounds(20,20);
        expandableListAdapter = new WorkPlanAdapter(getActivity().getApplicationContext(), listDataHeader, listDataChild);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        expandableListView.setAdapter(expandableListAdapter);

        prepareRequest(METHOD.GET_WORKPLAN,new HashMap<String, String>());

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /*
       Regresamos a nuestro MainActivity el valor de nuestro WORK_PLAN -> [ 1 ]
       para mostrar el icono asignado a nuestro Fragment.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);

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

        JSONObject  resp;

        try {
            resp        = new JSONObject(stringResponse);

            // Prepare data for ExpandableList
            if (resp.getString("method").equalsIgnoreCase(METHOD.GET_WORKPLAN.toString())){

                //listDataHeader.add("06/02/2015");

                JSONArray jsonArray = resp.getJSONArray("pdv_array");

                //Transformar fecha(int) en string
                JSONObject objFecha = jsonArray.getJSONObject(0);
                long fecha = Long.parseLong(objFecha.getString("vi_schedule_start"));
                Log.d("EN DECODERESPONSE WORKPLAN",fecha+"");
                String fecha_visita = new SimpleDateFormat("dd/MM/yyyy")
                        .format(new Date(fecha * 1000L));
                Log.d("EN DECODERESPONSE WORKPLAN",fecha_visita);
                listDataHeader.add(fecha_visita);

                List<Map<String,String>> visits = new ArrayList<Map<String,String>>();
                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject          obj     = jsonArray.getJSONObject(i);
                    Map<String,String>  map     = new HashMap<String, String>();
                    Iterator<?> keys = obj.keys();
                    while( keys.hasNext() ){
                        String key = (String)keys.next();
                        map.put(key,obj.getString(key));
                    }
                    visits.add(map);
                }

                listDataChild.put(listDataHeader.get(0), visits);
                expandableListAdapter.notifyDataSetChanged();
                expandableListView.expandGroup(0);
            }
            else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_PDV_INFO.toString())){


                JSONObject obj = new JSONObject(stringResponse);
                obj.put("id_visit",selectedData.get("id_visit"));
                obj.put("pdv_name",selectedData.get("pdv_name"));
                obj.put("calle",selectedData.get("calle"));
                obj.put("num_ext",selectedData.get("num_ext"));
                obj.put("localidad",selectedData.get("localidad"));
                obj.put("ciudad",selectedData.get("ciudad"));
                obj.put("estado",selectedData.get("estado"));
                obj.put("pais",selectedData.get("pais"));
                obj.put("latitud",selectedData.get("latitud"));
                obj.put("longitud",selectedData.get("longitud"));
                obj.put("status_visit",selectedData.get("status_visit"));
                obj.put("fecha programada inicial",selectedData.get("fecha programada inicial"));
                obj.put("fecha programada final",selectedData.get("fecha programada final"));


                Bundle bundle = new Bundle();
                bundle.putString("response",obj.toString());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragment = new FragmentCustomerWorkPlan();
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container, fragment, FragmentCustomerWorkPlan.TAG);
                fragmentTransaction.commit();

                ((MainActivity) getActivity()).depthCounter = 2;
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class WorkPlanAdapter extends BaseExpandableListAdapter {

        Context context;
        private List<String> listHeader;
        private HashMap<String, List<Map<String,String>>> lisHeaderDataChild;

        public WorkPlanAdapter(Context context, List<String> listHeader, HashMap<String, List<Map<String,String>>> lisHeaderDataChild){

            this.context            = context;
            this.listHeader         = listHeader;
            this.lisHeaderDataChild = lisHeaderDataChild;

        }

        @Override
        public int getGroupCount() {
            return this.listHeader.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listHeader.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.lisHeaderDataChild.get(this.listHeader.get(groupPosition))
                    .get(childPosition);
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Map<String,String> childData = (Map<String,String>) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_workplan, null);

            }
            Log.d(TAG, "Position: " + childPosition);

            convertView.setTag(childPosition);

            int real_position = childPosition+1;
            ((TextView) convertView.findViewById(R.id.txv_clients_preview_num)).setText(""+real_position);

            long fecha = Long.parseLong(childData.get("vi_schedule_start"));
            String hora_visita =  new SimpleDateFormat("HH:mm")
                    .format(new Date(fecha * 1000L));

            TextView txtAux = (TextView) convertView.findViewById(R.id.txt_pdv_name);
            txtAux.setText(childData.get("pdv_name"));

            txtAux = (TextView) convertView.findViewById(R.id.txt_pdv_time);
            txtAux.setText(hora_visita);

            //Para el status de la visita
            String status = "";
            switch (Integer.parseInt(childData.get("id_visit_status"))){
                case 1: //Agendada
                    status = getString(R.string.status_scheduled);
                    break;
                case 2: // Re agendada
                    status = getString(R.string.status_rescheduled);
                    break;
                default:
                    break;
            }

            txtAux = (TextView) convertView.findViewById(R.id.txt_pdv_status);
            txtAux.setText(status);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Clicked on Detail " + childPosition, Toast.LENGTH_LONG).show();
                    boolean fromWS = false;
                    if (fromWS){
                        selectedData                    = childData;
                        final Map<String, String> params = new HashMap<String, String>();
                        params.put("id_visit", childData.get("id_visit"));
                        prepareRequest(METHOD.GET_PDV_INFO,params);
                    }
                    else{
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragment = new FragmentCustomerWorkPlan();

                        Bundle bundle = new Bundle();

                        JSONObject obj=new JSONObject(childData);
                        bundle.putString("pdv",obj.toString());

                        fragment.setArguments(bundle);

                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.container, fragment, FragmentCustomerWorkPlan.TAG);
                        fragmentTransaction.commit();
                    }
                    ((MainActivity) getActivity()).depthCounter = 2;

                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.lisHeaderDataChild.get(this.listHeader.get(groupPosition)).size();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {

            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            String headerTitle = (String) getGroup(groupPosition);


            if (convertView == null) {

                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.group_workplan, null);

            }

            ImageView flecha_grupo = (ImageView)convertView.findViewById(R.id.icon_workplan_expandableExpand);
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.txt_workplan_header);
            flecha_grupo.setImageResource(R.drawable.flecha_cerrada);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            //Cambiando nuestra fleca de abierto a cerrado
            if(isExpanded){
                flecha_grupo.setImageResource(R.drawable.flecha_abierta);

            }else{
                flecha_grupo.setImageResource(R.drawable.flecha_cerrada);

            }

            return convertView;
        }



        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }


}
