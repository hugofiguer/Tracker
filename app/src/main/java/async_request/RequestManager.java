package async_request;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;

import com.sellcom.tracker.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RequestManager implements ResponseListenerInterface {

    //Etapa de pruebas... TEST_MODE
    public  final   boolean                                 TEST_MODE          = true;
    //public  final   boolean                                 TEST_MODE          = false;

    public  final String LOG_TAG_MANAGER    = "requestManager";
    public  final String LOG_TAG_REQUEST    = "asyncRequest";

    public  final String API_URL 	       = "http://172.20.111.69:8880/app_develop/Preventa-Central-master/api.php";
    //public  final String API_URL 	       = "http://187.237.42.162:8880/app_develop/Preventa-Central-master/api.php";
    //public  final String API_URL 	       = "http://54.187.219.128/ragasa/sicmobile/api.php";  //Ragasa


    private static RequestManager manager;
    private Activity activity;
    private         UIResponseListenerInterface             listener;
    private         METHOD                                  method;

    private ProgressDialog progressDialog;

    private String user_token;

    private RequestManager(){
    }
    //si no existe mi estancía estatica la genera, de lo contrario la regresa
    public static synchronized RequestManager sharedInstance(){
        if (manager == null)
            manager = new RequestManager();
        return manager;
    }

    /** STORE OF USER PREFERENCES **/
    //Para guardar en preferencias.....
    public void saveInPreferencesKeyAndValue(String key, String value){
        SharedPreferences sharedPref        = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor     = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getPreferencesValueForKey(String key){
        SharedPreferences sharedPref        = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key,"CLEAR");
    }

    public void                         setActivity(Activity activity)  {this.activity    = activity;}
    public Activity getActivity()                   {return activity;}

    public void                         setUser_token(String user_token){this.user_token    = user_token;}
    public String getUser_token()                 {return user_token;}
    public void                         clearUser_token()               {user_token    = null;
    }

    public void                         setListener(UIResponseListenerInterface listener){   this.listener = listener;}
    public UIResponseListenerInterface  getListener(){return listener;}

    //Mi mensaje de error..... PARA MI DIALOG
    public void showErrorDialog(String errorMessage, Context context){
        Log.d(LOG_TAG_MANAGER, "Error message: " + errorMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //INFORMACION DE MI DIALOG, DEPENDIENDO EL MODULO DE USO...
    //Aqui si agrupan todos los mensajes de loading, empatados con el metodo que los manda

    public void showLoadingDialog(){
        String dialogMessage    = "";
        Log.e("ABCDEFG","EN SHOWLOADINGDIALOG");
        switch (method){
            case LOGIN:
                dialogMessage   = activity.getString(R.string.logging_in);
                break;
            case GET_WORKPLAN:
                dialogMessage   = activity.getString(R.string.req_man_retrieving_pdvs);
                break;
            case GET_PDV_INFO:
                dialogMessage   = activity.getString(R.string.req_man_retrieving_pdv_detail);
                break;
            default:
                break;
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage(dialogMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        progressDialog.dismiss();
    }

    public void makeRequestWithDataAndMethod(Map<String,String> reqData, METHOD method){
        Log.e("ABCDEFG",method.toString());
        this.method                 = method;
        showLoadingDialog();
        final AsyncRequest req      = new AsyncRequest(activity, reqData, this);

        /* DUMMY request mode info */
        req.method                  = method;
        /***************************/
        req.execute(null,null,null);
    }

    @Override
    public void responseServiceToManager(JSONObject jsonResponse) {
        dismissProgressDialog();
        try {
            if(jsonResponse.getString("success").equalsIgnoreCase("true")){
                // Decode the json object
                Log.d(LOG_TAG_MANAGER, jsonResponse.toString());
                Log.e("ABCDEFG","EN RESPONSESERVICETOMANAGER");
                listener.decodeResponse(jsonResponse.toString());
            }/*
            else{
                if (jsonResponse.getString("method").equalsIgnoreCase(METHOD.GET_PRODUCTS.toString())){
                    Log.d(LOG_TAG_MANAGER, jsonResponse.toString());
                    listener.decodeResponse(jsonResponse.toString());
                }
                else{
                    showErrorDialog(jsonResponse.getString("resp"), this.activity);
                }
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorDialog(activity.getString(R.string.req_man_error_contacting_service), this.activity);
        }
    }

    public class AsyncRequest extends AsyncTask<Void,Void,JSONObject> {
        Activity activity;
        Map<String, String> requestData;
        ResponseListenerInterface   listener;

        /* DUMMY request mode info */
        METHOD                      method;

        public AsyncRequest(Activity activity, Map<String, String> requestData, ResponseListenerInterface listener){
            this.activity 	       	= activity;
            this.requestData        = requestData;
            this.listener    		= listener;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Log.d(LOG_TAG_REQUEST, "Request data:" + requestData.toString());
            JSONObject jsonResponse = null;
            if (TEST_MODE){
                try {
                    Thread.sleep(3000);
                    jsonResponse = new JSONObject();
                    try {
                        jsonResponse.put("method",method.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (method == METHOD.LOGIN){
                        try {
                            jsonResponse.put("success",true);
                            jsonResponse.put("resp","OK");
                            jsonResponse.put("token","1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (method == METHOD.GET_WORKPLAN){
                        try {
                            jsonResponse.put("success",true);
                            jsonResponse.put("date","01/01/2015");

                            JSONArray pdvs = new JSONArray();

                            for (int i=1; i<=5; i++){
                                JSONObject pdv = new JSONObject();
                                pdv.put("id_pdv",i);
                                pdv.put("id_visit",i);
                                pdv.put("vi_schedule_start","1423502820");
                                pdv.put("pdv_name","Tiendita la Escondida "+i);
                                pdv.put("id_visit_status","1");

                                pdvs.put(pdv);
                            }

                            jsonResponse.put("pdv_array",pdvs);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (method == METHOD.GET_PDV_INFO){
                        try {
                            jsonResponse.put("success",true);
                            jsonResponse.put("resp","OK");

                            JSONObject pdv = new JSONObject();
                            pdv.put("id_pdv","1");
                            pdv.put("pdv_name","Tiendita la Escondida");
                            pdv.put("calle","Insurgentes");
                            pdv.put("num_ext","45");
                            pdv.put("localidad","centro");
                            pdv.put("ciudad","cuernavaca");
                            pdv.put("estado","morelos");
                            pdv.put("pais","mexico");
                            pdv.put("latitud","19.39012180000000000000");
                            pdv.put("longitud","-99.29144009999999000000");
                            pdv.put("status_visit","1");
                            pdv.put("fecha programada inicial","1423502820");
                            pdv.put("fecha programada final","1423502820");

                            jsonResponse.put("pdv_info",pdv);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            jsonResponse.put("success",true);
                            jsonResponse.put("date","01/01/2015");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.e("ABCDEFG","EN HTTPCLIENT");
                HttpClient httpclient   = new DefaultHttpClient();
                HttpPost httppost       = new HttpPost(API_URL);

                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>(requestData.size());

                    for (Map.Entry<String, String> entry : requestData.entrySet())
                        params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

                    httppost.setEntity(new UrlEncodedFormEntity(params));

                    HttpResponse response       = httpclient.execute(httppost);
                    String strResponse    = EntityUtils.toString(response.getEntity());
                    Log.d(LOG_TAG_REQUEST, "Response: " + strResponse);
                    try {
                        jsonResponse            = new JSONObject(strResponse);
                        jsonResponse.put("method",method.toString());
                        Log.d(LOG_TAG_REQUEST, "jsonResponse: " + jsonResponse.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (ClientProtocolException e) {
                    try {
                        jsonResponse    = new JSONObject();
                        jsonResponse.put("error", "Network");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    try {
                        jsonResponse    = new JSONObject();
                        jsonResponse.put("error", "Info");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return jsonResponse;
        }

        protected void onPostExecute(JSONObject jsonResponse) {
            Log.d(LOG_TAG_REQUEST, "jsonResponse_post: " + jsonResponse.toString());
            Log.e("ABCDEFG","EN ONPOSTEXECUTE");
            listener.responseServiceToManager(jsonResponse);
        }
    }
}