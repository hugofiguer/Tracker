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
import util.Utilities;


public class FragmentWorkPlan extends Fragment implements UIResponseListenerInterface{

    final static public String TAG = "TAG_FRAGMENT_WORK_PLAN";
    private int ChildClickStatus=-1;

    static Context context;
    public Fragment fragment;
    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    View view;

    private Utilities utilities;
    public WorkPlanAdapter                  expandableListAdapter;
    public ExpandableListView expandableListView;
    public ImageView groupExpandlistIconCollapse;
    public ImageView                        grouoExpandlistIconExpand;

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
        utilities = new Utilities(getActivity());

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

                JSONArray jsonArray = resp.getJSONArray("pdv_array");

                //Transformar fecha(int) en string
                JSONObject objFecha = jsonArray.getJSONObject(0);

                listDataHeader.add(utilities.getFormatDate(objFecha.getString("vi_schedule_start")));

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
            else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_INFO_VISIT.toString())){


                JSONObject obj = new JSONObject(stringResponse);
                obj.put("id_visit",selectedData.get("id_visit"));
                obj.put("pdv_name",selectedData.get("pdv_name"));
                obj.put("ad_street",selectedData.get("ad_street"));
                obj.put("ad_ext_num",selectedData.get("ad_ext_num"));
                obj.put("ad_locality",selectedData.get("ad_locality"));
                obj.put("ad_city",selectedData.get("ad_city"));
                obj.put("st_state",selectedData.get("st_state"));
                obj.put("cnt_country",selectedData.get("cnt_country"));
                obj.put("ad_latitude",selectedData.get("ad_latitude"));
                obj.put("ad_longitude",selectedData.get("ad_longitude"));
                obj.put("id_visit_status",selectedData.get("id_visit_status"));
                obj.put("vi_schedule_start",selectedData.get("vi_schedule_start"));
                obj.put("vi_schedule_end",selectedData.get("fecha programada final"));


                Bundle bundle = new Bundle();
                bundle.putString("response",obj.toString());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragment = new FragmentCustomerWorkPlan();
                fragment.setArguments(bundle);

                Log.e("En WORKPLAN","----------------------------------");

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

            TextView txtAux = (TextView) convertView.findViewById(R.id.txt_pdv_name);
            txtAux.setText(childData.get("pdv_name"));

            txtAux = (TextView) convertView.findViewById(R.id.txt_pdv_time);
            txtAux.setText(utilities.getFormatTime(childData.get("vi_schedule_start")));

            txtAux = (TextView) convertView.findViewById(R.id.txt_pdv_status);
            txtAux.setText(utilities.getTypeStatus(childData.get("id_visit_status")));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Clicked on Detail " + childPosition, Toast.LENGTH_LONG).show();
                    boolean fromWS = true;
                    if (fromWS){
                        selectedData                    = childData;
                        final Map<String, String> params = new HashMap<String, String>();
                        params.put("id_visit", childData.get("id_visit"));
                        prepareRequest(METHOD.GET_INFO_VISIT,params);
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
