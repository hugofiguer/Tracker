package com.sellcom.tracker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
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
import util.ListViewItem;

public class FragmentForms extends Fragment implements UIResponseListenerInterface{

    final static public String  TAG = "TAG_FRAGMENT_FORM";
    private LayoutInflater      layoutInflater;
    private View[]                view;
    private RadioGroup[]          rg;
    private TextView[]            textView;
    private EditText[]            editText;
    private Spinner[]             spinner;
    private CheckBox[]            checkBox;
    private TextView              txt_form_name;

    private LinearLayout        linearContainer;
    private LinearLayout[]        containerChecks;
    //private FormAdapter         formAdapter;
    //private GridView            gridView;
    private String              id_visit,nameForm=".-?",answerOption;
    private Context             context;
    private int                 sizeForm;
    private String[]            arrayMultipleOptions,arrayChecks;

    private ListViewItem[] listViewItems;

    private Map<String,String>  dataForm;

    public static final String TYPE_BINARY = "Binary";
    public static final String TYPE_TEXT   = "Text";
    public static final String TYPE_RADIO  = "Radio Option";
    public static final String TYPE_NUMBER = "Number";
    public static final String TYPE_EMAIL  = "E-mail";
    public static final String TYPE_DATE   = "Date";
    public static final String TYPE_FLOAT  = "Float";
    public static final String TYPE_CHECK  = "Check Option";
    public static final String TYPE_SELECT = "Select Option";

    //private ViewHolderForBinary[] viewHolderForBinary = null;

    private  ListViewItem[] items = null;

    public FragmentForms() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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
        linearContainer = (LinearLayout)vista.findViewById(R.id.linearContainer);
        txt_form_name   = (TextView)vista.findViewById(R.id.txt_form_name);

        dataForm = new HashMap<String, String>();

        prepareRequest(METHOD.GET_FORM,new HashMap<String, String>());

        return vista;
    }

    public void OnInit(JSONArray jsonArray){

        JSONArray jsonArray1 = jsonArray;
        sizeForm = jsonArray1.length();
        items = new ListViewItem[sizeForm];

        try {

            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject object = jsonArray1.getJSONObject(i);

                if(nameForm.equals(".-?")){
                    nameForm = object.getString("ft_name");
                }

                if(object.getString("dt_data_type").equals(TYPE_TEXT)){
                    Log.e(TAG,"1");


                    items[i] = new ListViewItem(TYPE_TEXT,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_NUMBER)){
                    Log.e(TAG,"2");
                    items[i] = new ListViewItem(TYPE_NUMBER,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_EMAIL)){
                    Log.e(TAG,"3");
                    items[i] = new ListViewItem(TYPE_EMAIL,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_FLOAT)){
                    Log.e(TAG,"4");
                    items[i] = new ListViewItem(TYPE_FLOAT,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_BINARY)){
                    Log.e(TAG,"5");
                    items[i] = new ListViewItem(TYPE_BINARY,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_RADIO)){
                    Log.e(TAG,"6");
                    items[i] = new ListViewItem(TYPE_RADIO,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_CHECK)){
                    Log.e(TAG,"7");
                    items[i] = new ListViewItem(TYPE_CHECK,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_SELECT)){
                    Log.e(TAG,"8");
                    items[i] = new ListViewItem(TYPE_SELECT,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_DATE)){
                    Log.e(TAG,"9");
                    items[i] = new ListViewItem(TYPE_DATE,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }

            }

            txt_form_name.setText("" + nameForm);


            view            = new View[sizeForm];
            rg              = new RadioGroup[sizeForm];
            textView        = new TextView[sizeForm];
            editText        = new EditText[sizeForm];
            spinner         = new Spinner[sizeForm];
            containerChecks = new LinearLayout[sizeForm];

            for (int i = 0; i < sizeForm; i++) {

                if (items[i].getType().equals(TYPE_BINARY)){
                    view[i]     = layoutInflater.inflate(R.layout.item_form_question_binary,null);
                    linearContainer.addView(view[i]);

                    textView[i] = (TextView) view[i].findViewById(R.id.txt_question_binary);
                    textView[i].setText(items[i].getQuestion());

                    rg[i]       = (RadioGroup) view[i].findViewById(R.id.rgp_binary);
                }else if(items[i].getType().equals(TYPE_TEXT)){
                    view[i]     = layoutInflater.inflate(R.layout.item_form_question_text,null);
                    linearContainer.addView(view[i]);

                    textView[i] = (TextView) view[i].findViewById(R.id.txt_question_text);
                    textView[i].setText(items[i].getQuestion());

                    editText[i] = (EditText) view[i].findViewById(R.id.edt_answer_text);
                }else if(items[i].getType().equals(TYPE_RADIO) || items[i].getType().equals(TYPE_SELECT)){
                    view[i]     = layoutInflater.inflate(R.layout.item_form_question_radio,null);
                    linearContainer.addView(view[i]);

                    String options = items[i].getAop_option();
                    arrayMultipleOptions = options.split(",");

                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(context,
                            android.R.layout.simple_spinner_dropdown_item,
                            arrayMultipleOptions);

                    textView[i] = (TextView) view[i].findViewById(R.id.txt_question_radio);
                    textView[i].setText(items[i].getQuestion());

                    spinner[i]  = (Spinner) view[i].findViewById(R.id.spn_answer_radio);
                    spinner[i].setAdapter(spinnerArrayAdapter);
                }else if(items[i].getType().equals(TYPE_NUMBER)){
                    view[i]     = layoutInflater.inflate(R.layout.item_form_question_number,null);
                    linearContainer.addView(view[i]);

                    textView[i] = (TextView) view[i].findViewById(R.id.txt_question_number);
                    textView[i].setText(items[i].getQuestion());

                    editText[i] = (EditText) view[i].findViewById(R.id.edt_answer_number);
                }else if (items[i].getType().equals(TYPE_EMAIL)) {
                    view[i] = layoutInflater.inflate(R.layout.item_form_question_email,null);
                    linearContainer.addView(view[i]);

                    textView[i] = (TextView) view[i].findViewById(R.id.txt_question_email);
                    textView[i].setText(items[i].getQuestion());

                    editText[i] = (EditText) view[i].findViewById(R.id.edt_answer_email);
                } else if (items[i].getType().equals(TYPE_FLOAT)) {
                    view[i] = layoutInflater.inflate(R.layout.item_form_question_float, null);
                    linearContainer.addView(view[i]);

                    textView[i] = (TextView) view[i].findViewById(R.id.txt_question_float);
                    textView[i].setText(items[i].getQuestion());

                    editText[i] = (EditText) view[i].findViewById(R.id.edt_answer_float);
                }else if (items[i].getType().equals(TYPE_DATE)){

                    view[i] = layoutInflater.inflate(R.layout.item_form_question_date, null);
                    linearContainer.addView(view[i]);

                    textView[i] = (TextView) view[i].findViewById(R.id.txt_question_date);
                    textView[i].setText(items[i].getQuestion());

                }else if (items[i].getType().equals(TYPE_CHECK)){
                    String options = items[i].getAop_option();
                    arrayChecks = options.split(",");
                    checkBox = new CheckBox[arrayChecks.length];

                    view[i] = layoutInflater.inflate(R.layout.item_form_question_check,null);
                    linearContainer.addView(view[i]);

                    textView[i] = (TextView) view[i].findViewById(R.id.txt_question_check);
                    textView[i].setText(items[i].getQuestion());

                    containerChecks[i] = (LinearLayout) view[i].findViewById(R.id.containerChecks);

                    for (int j = 0; j < checkBox.length; j++) {
                        checkBox[j] = new CheckBox(context);
                        checkBox[j].setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        checkBox[j].setText(arrayChecks[j]);
                        containerChecks[i].addView(checkBox[j]);
                    }

                }


            }

            listViewItems = items;


        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    public void sendDataForm(){

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();

        try{

            for (int i = 0; i < listViewItems.length; i++) {
                if (listViewItems[i].getType().equals(TYPE_BINARY)){

                    rg[i].setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                            if(checkedId == R.id.rbtn_binary1){
                               answerOption = "si";
                            }else if(checkedId == R.id.rbtn_binary2){
                                answerOption = "no";
                            }
                        }
                    });

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("aop_option",answerOption);
                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_TEXT)){
                    Log.e("Contenido del textview: ",editText[i].getText().toString());
                }else if(listViewItems[i].getType().equals(TYPE_RADIO) || listViewItems[i].getType().equals(TYPE_SELECT)){

                    answerOption =  spinner[i].getSelectedItem().toString();

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("aop_option",answerOption);
                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_NUMBER)){

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("aop_option",editText[i].getText().toString());
                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_CHECK)){

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("aop_option","10,20,30");
                    array.put(jsonObject);


                }else if(listViewItems[i].getType().equals(TYPE_DATE)){

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("aop_option","12-01-2015 10:35");
                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_FLOAT)){

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("aop_option",editText[i].getText().toString());
                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_EMAIL)){

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("aop_option",editText[i].getText().toString());
                    array.put(jsonObject);

                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }


        JSONObject object = new JSONObject();
        try {
            object.put("answer_form",array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("answer_form",array.toString());
        Log.e("answer_form",object.toString());


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Toast.makeText(getActivity(), "Enviado", Toast.LENGTH_SHORT).show();
                sendDataForm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.send, menu);
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
