package com.sellcom.tracker_interno;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;

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
import util.ListViewItem;

public class FragmentQuestionsForm extends Fragment implements UIResponseListenerInterface{

    final static public String  TAG = "TAG_FRAGMENT_QUESTION_FORM";
    private LayoutInflater      layoutInflater;
    private View[]                view;
    private RadioGroup[]          rg;
    private TextView[]            textView;
    private EditText[]            editText;
    private Spinner[]             spinner;
    private CheckBox[]            checkBox;
    private DatePicker[]          datePicker;
    private TimePicker[]          timePicker;
    private TextView              txt_form_name;

    private LinearLayout linearContainer;
    private LinearLayout[]        containerChecks;
    //private FormAdapter         formAdapter;
    //private GridView            gridView;
    private String              id_visit,nameForm=".-?",answerOption;
    private Context context;
    private int                 sizeForm;
    private String[]            arrayMultipleOptions,arrayChecks;

    //Datos del Formulario elegido
    private String              id_data_form,dtf_name,dtf_obligatory,questions;

    private ListViewItem[] listViewItems;

    private Map<String,String> dataForm;

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

    public FragmentQuestionsForm() {
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
        View vista = inflater.inflate(R.layout.fragment_questions_forms, container, false);

        linearContainer = (LinearLayout)vista.findViewById(R.id.linearContainer);
        txt_form_name   = (TextView)vista.findViewById(R.id.txt_form_name);

        id_visit = getArguments().getString("id_visit");
        id_data_form = getArguments().getString("id_data_form");
        dtf_name = getArguments().getString("dtf_name");
        questions = getArguments().getString("questions");

        dataForm = new HashMap<String, String>();

        txt_form_name.setText("" + dtf_name);


        JSONArray array     = null;
        try {
            array = new JSONArray(questions);
            OnInit(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return vista;
    }

    public void OnInit(JSONArray jsonArray){

        JSONArray jsonArray1 = jsonArray;
        sizeForm = jsonArray1.length();
        items = new ListViewItem[sizeForm];

        try {

            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject object = jsonArray1.getJSONObject(i);

                if(object.getString("dt_data_type").equals(TYPE_TEXT)){
                    items[i] = new ListViewItem(TYPE_TEXT,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_NUMBER)){
                    items[i] = new ListViewItem(TYPE_NUMBER,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_EMAIL)){
                    items[i] = new ListViewItem(TYPE_EMAIL,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_FLOAT)){
                    items[i] = new ListViewItem(TYPE_FLOAT,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_BINARY)){
                    items[i] = new ListViewItem(TYPE_BINARY,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_RADIO)){
                    items[i] = new ListViewItem(TYPE_RADIO,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_CHECK)){
                    items[i] = new ListViewItem(TYPE_CHECK,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_SELECT)){
                    items[i] = new ListViewItem(TYPE_SELECT,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }else if(object.getString("dt_data_type").equals(TYPE_DATE)){
                    items[i] = new ListViewItem(TYPE_DATE,object.getString("que_question"),object.getString("id_question"),object.getString("aop_option"));
                }

            }


            view            = new View[sizeForm];
            rg              = new RadioGroup[sizeForm];
            datePicker      = new DatePicker[sizeForm];
            timePicker      = new TimePicker[sizeForm];
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
                    arrayMultipleOptions = options.split(";");

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

                    datePicker[i] = (DatePicker)view[i].findViewById(R.id.datePicker);
                    timePicker[i] = (TimePicker)view[i].findViewById(R.id.timePicker);


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

        getActivity().onBackPressed();
        getActivity().onBackPressed();


    }


    public void sendDataForm(){

        JSONObject jsonObject = null;
        JSONArray array = new JSONArray();

        try{

            for (int i = 0; i < listViewItems.length; i++) {


                jsonObject = new JSONObject();
                if (listViewItems[i].getType().equals(TYPE_BINARY)){

                    int selectedId = rg[i].getCheckedRadioButtonId();

                    if(selectedId == R.id.rbtn_binary1){
                        answerOption = "si";
                    }else if(selectedId == R.id.rbtn_binary2){
                        answerOption = "no";
                    }

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("answer",answerOption);

                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_TEXT)){
                    answerOption = editText[i].getText().toString();
                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("answer",answerOption);

                    array.put(jsonObject);
                }else if(listViewItems[i].getType().equals(TYPE_RADIO) || listViewItems[i].getType().equals(TYPE_SELECT)){

                    answerOption =  spinner[i].getSelectedItem().toString();

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("answer",answerOption);

                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_NUMBER)){

                    answerOption = editText[i].getText().toString();
                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("answer",editText[i].getText().toString());

                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_CHECK)){
                    String answer = "";
                    int cont= 0;
                    for(int j = 0; j < checkBox.length; j++){
                        if(checkBox[j].isChecked()){
                            answer = answer + checkBox[j].getText().toString();

                            if(cont>=0 && j != checkBox.length-1){
                                answer = answer + ";";
                            }
                            cont++;
                        }
                    }

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("answer",answer);

                    array.put(jsonObject);


                }else if(listViewItems[i].getType().equals(TYPE_DATE)){

                    String answer = getNewDate(datePicker[i].getDayOfMonth(),(datePicker[i].getMonth() + 1),datePicker[i].getYear())+" "+getNewTime(timePicker[i].getCurrentHour(), timePicker[i].getCurrentMinute());

                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("answer",answer);

                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_FLOAT)){

                    answerOption = editText[i].getText().toString();
                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("answer",editText[i].getText().toString());

                    array.put(jsonObject);

                }else if(listViewItems[i].getType().equals(TYPE_EMAIL)){

                    answerOption = editText[i].getText().toString();
                    jsonObject.put("id_question",items[i].getId_question());
                    jsonObject.put("answer",editText[i].getText().toString());

                    array.put(jsonObject);

                }

            }

        }catch (JSONException e){
            e.printStackTrace();
        }


        JSONObject objectQuestions  = new JSONObject();
        JSONArray arrayForms        = new JSONArray();
        try {
            objectQuestions.put("questions",array);
            objectQuestions.put("id_visit",id_visit);
            objectQuestions.put("id_data_form",id_data_form);
            arrayForms.put(objectQuestions);
            String token      = Session.getSessionActive(getActivity()).getToken();
            String username   = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
            dataForm.put("request", METHOD.SET_FORM.toString());
            dataForm.put("user", username);
            dataForm.put("token", token);
            dataForm.put("form",arrayForms.toString());


            prepareRequest(METHOD.SET_FORM,dataForm);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String getNewTime(int hour, int minutes){

        String newTime;

        if(hour<10 && minutes <10) {
            newTime = "0" + hour + ":0" + minutes;
        }else if (minutes<10){
            newTime = hour + ":0" + minutes;
        }else if(hour<10){
            newTime = "0" +hour + ":" + minutes;
        }else{
            newTime = hour + ":" + minutes;
        }

        return newTime;

    }

    public String getNewDate(int day, int month, int year){

        String newDate;

        if(day<10 && month <10) {
            newDate = year + "-0" + (month + 1) + "-0" + day;
        }else if (day<10){
            newDate = year + "-" + (month + 1) + "-0" + day;
        }else if(month<10){
            newDate = year + "-0" + (month + 1) + "-" + day;
        }else{
            newDate = year + "-" + (month + 1) + "-" + day;
        }

        return newDate;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
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
                }else{

                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }


    }
}
