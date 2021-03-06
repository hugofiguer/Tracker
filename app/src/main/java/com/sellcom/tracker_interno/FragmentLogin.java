package com.sellcom.tracker_interno;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Permission;
import database.models.Profile;
import database.models.Session;
import database.models.User;
import location.GPSTracker;
import util.Utilities;



public class FragmentLogin extends Fragment implements View.OnClickListener, UIResponseListenerInterface {

    private Context context;
    private EditText txt_emailUser,txt_passwordUser;
    private Button boton;
    private String textEmail,textPassword,usr_name,usr_email;
    int user_id;


    public FragmentLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        context = getActivity();

        boton = (Button)view.findViewById(R.id.sign_in_button);
        boton.setOnClickListener(this);

        txt_emailUser = (EditText)view.findViewById(R.id.emailUser);
        txt_emailUser.setOnClickListener(this);
        txt_passwordUser = (EditText)view.findViewById(R.id.passwordUser);
        txt_passwordUser.setOnClickListener(this);


        return view;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.sign_in_button:
                textEmail       = txt_emailUser.getText().toString();
                textPassword    = txt_passwordUser.getText().toString();


                if (textEmail.isEmpty()) {
                    txt_emailUser.setError(getString(R.string.error_empty_field));
                    txt_emailUser.requestFocus();
                    return;
                }
                else if (textPassword.isEmpty()){
                    txt_passwordUser.setError(getString(R.string.error_empty_field));
                    txt_passwordUser.requestFocus();
                    return;
                }else

                Utilities.hideKeyboard(context, txt_passwordUser);

                /**** Request manager stub
                 * 1. Recover data from UI
                 * 2. Set the RequestManager listener to 'this'
                 * 3. Send the request (Via RequestManager)
                 * 4. Wait for it
                 */

                // 0

                Location myLocation = new GPSTracker(getActivity()).getCurrentLocation();


                final Map<String, String> params = new HashMap<String, String>();
                params.put("request", METHOD.LOGIN.toString());
                params.put("user", textEmail);
                params.put("password", textPassword);
                try{
                    params.put("hil_x",""+myLocation.getLatitude());
                    params.put("hil_y",""+myLocation.getLongitude());
                }catch (Exception e){
                    Toast.makeText(getActivity(), getActivity().getString(R.string.activate_gps_service), Toast.LENGTH_SHORT).show();
                    return;
                }


                //2
                RequestManager.sharedInstance().setListener(this);

                //3
                RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, METHOD.LOGIN);
            break;
        }

    }

    public void startMainActivity(){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("usr_name",usr_name);
        intent.putExtra("usr_email",usr_email);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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

        Log.d("LOG_LOGIN_FRAGMENT", stringResponse);
        JSONObject resp;

        try {
            resp        = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.LOGIN.toString())) {

                    if(resp.getString("success").equalsIgnoreCase("true")){
                        Log.e("ABCDEFG","EN IFDECODERESPONSE");
                        String textToken = resp.getString("token");

                        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                        telephonyManager.getDeviceId();

                        // Force to salesman profile
                        int profileId = 2;
                        String profileName = "Level 1";
                        Cursor user = User.getUserForEmail(context, textEmail);
                        if (user != null && user.getCount() > 0) {
                            user_id = user.getInt(user.getColumnIndex(User.ID));

                            Session.closeSession(context);
                            Session.updateToken(context, 1, "fecha", textToken, android.os.Build.MODEL + " - " + telephonyManager.getDeviceId(), user_id);

                        } else {

                            long userId = User.insert(getActivity(), textEmail, textPassword, profileId, 0);
                            Session.closeSession(context);
                            Session.insert(context, 1, "date", textToken, "001", (int) userId);

                            Profile.insert(context, profileId, profileName);

                            if (profileId == 1)
                                Permission.setFullPermission(context, profileName);
                            else
                                Permission.setBasicPermission(context, profileName);
                        }

                        prepareRequest(METHOD.GET_USER_INFO,new HashMap<String, String>());
                    }


            }else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_USER_INFO.toString())) {

                    if(resp.getString("success").equalsIgnoreCase("true")){

                        JSONArray act_array = resp.getJSONArray("user_info");
                        String strArray     = act_array.toString();

                        try {
                            JSONArray jsonArray = new JSONArray(strArray);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object          = jsonArray.getJSONObject(i);
                                usr_email = object.getString("usr_email");
                                usr_name = object.getString("usr_name");

                            }

                            startMainActivity();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

            }

            } catch (JSONException e) {
                e.printStackTrace();
            }

    }
}
