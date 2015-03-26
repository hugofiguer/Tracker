package com.sellcom.tracker_interno;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;
import util.Utilities;


public class FragmentEvidence extends Fragment implements UIResponseListenerInterface,View.OnClickListener, FragmentDialogEvidence.setSetImgPhoto, FragmentDialogVisitActivities.activitySuccess{

    final static public String TAG = "TAG_FRAGMENT_EVIDENCE";
    static Context context;
    private Button btnAcept,btnCancel;
    private ImageView imgCamera, imgPhoto;
    private FragmentManager fragmentManager;
    private Spinner spinnerEvidenceType = null, spinnerActivity;
    private String arraySpinnerEvidenceType[],arraySpinnerActivities[],positionEvidenceType[],positionIdActivity[];
    private int positionActivity[];
    private ArrayAdapter<String> arrayAdepter;
    List<Map<String,String>> elementsList;
    private String id_visit,imageEncode = null,nameFile,jsonObjectActivities;
    private static final int THUMBNAIL_ID = 1;
    private Bitmap image;
    private Uri mImageUri = null;
    private Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_evidence, container, false);

        //Directorio temporal donde se guardara la imagen capturada
        nameFile = Environment.getExternalStorageDirectory() + File.separator +".photo/"+ "test.jpeg";


        elementsList   = new ArrayList<Map<String, String>>();
        id_visit = getArguments().getString("id_visit");
        jsonObjectActivities = getArguments().getString("jsonObjectActivities"); //informacion de las actividades

        btnAcept = (Button)view.findViewById(R.id.btnAccept);
        btnAcept.setOnClickListener(this);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        imgCamera = (ImageView)view.findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);
        imgPhoto = (ImageView)view.findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(this);
        spinnerEvidenceType = (Spinner)view.findViewById(R.id.spinnerEvidenceType);
        spinnerActivity = (Spinner)view.findViewById(R.id.spinnerActivity);

        prepareRequest(METHOD.GET_CAT_EV,new HashMap<String, String>());
        getActivities(jsonObjectActivities);

        return view;
    }


    public void getActivities(String jsonObjectActivities){

        JSONObject resp;

        try {
            resp        = new JSONObject(jsonObjectActivities);

            if (resp.getString("method").equalsIgnoreCase(METHOD.GET_ACTIVITIES.toString())) {
                JSONArray act_array = resp.getJSONArray("info_activities");
                String strArray     = act_array.toString();

                try {
                    JSONArray jsonArray = new JSONArray(strArray);

                    arraySpinnerActivities = new String[jsonArray.length()];


                    int cont = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object          = jsonArray.getJSONObject(i);
                        elementsList.add(RequestManager.sharedInstance().jsonToMap(object));
                        if(Integer.parseInt(elementsList.get(i).get("acv_time"))==0) {
                            cont++;
                        }
                    }
                    positionIdActivity = new String[cont];//arreglo que guarda el id de la actividad seleccionada en el spinner de actividades
                                                            //posteriormente se enviara esta informacion junto con la foto capturada
                    arraySpinnerActivities = new String[cont];
                    positionActivity = new int[cont];

                    cont = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if(Integer.parseInt(elementsList.get(i).get("acv_time"))==0) {
                            positionIdActivity[cont] = elementsList.get(i).get("id_activity");
                            arraySpinnerActivities[cont] = elementsList.get(i).get("act_name");
                            positionActivity[cont] = i;
                            cont++;
                        }
                    }


                    arrayAdepter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,arraySpinnerActivities);
                    arrayAdepter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerActivity.setAdapter(arrayAdepter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
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


        try {
            resp        = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.GET_CAT_EV.toString())){
                JSONArray act_array = resp.getJSONArray("evidence_type");
                String strArray     = act_array.toString();

                try {
                    JSONArray jsonArray = new JSONArray(strArray);

                    arraySpinnerEvidenceType = new String[jsonArray.length()];
                    positionEvidenceType = new String[jsonArray.length()];//arreglo que guarda el id del tipo de evidencia seleccionada en el spinner de tipo de evidencia
                                                                          //posteriormente se enviara esta informacion junto con la foto capturada

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        positionEvidenceType[i] = object.getString("id_evidence_type");
                        arraySpinnerEvidenceType[i] = object.getString("et_evidence_type");
                    }

                    //Se agregan al spinner los tipos de evidencia que se pueden enviar al servidor
                    arrayAdepter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,arraySpinnerEvidenceType);
                    arrayAdepter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEvidenceType.setAdapter(arrayAdepter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Utilities.showErrorDialog(getActivity().getString(R.string.req_man_error_contacting_service), getActivity());

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgCamera:
                Log.d("CLICK","-- - - - - - - -");
                takePhoto();
                break;
            case R.id.imgPhoto:
                if (imgPhoto.getDrawable() != null){
                    openDialog(image);
                }
                break;
            case R.id.btnAccept:
                if(spinnerEvidenceType.getSelectedItem() != null){
                    if(imageEncode != null){

                            if(Integer.parseInt(elementsList.get(positionActivity[spinnerActivity.getSelectedItemPosition()]).get("acv_time"))==0) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id_activity", elementsList.get(positionActivity[spinnerActivity.getSelectedItemPosition()]).get("id_activity"));
                                bundle.putString("act_name",elementsList.get(positionActivity[spinnerActivity.getSelectedItemPosition()]).get("act_name"));
                                bundle.putString("id_visit",id_visit);
                                //bundle.putString("id_activity",positionIdActivity[spinnerActivity.getSelectedItemPosition()]);
                                bundle.putString("id_evidence_type",positionEvidenceType[spinnerEvidenceType.getSelectedItemPosition()]);
                                bundle.putString("foto",imageEncode);

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentDialogVisitActivities dialogo = new FragmentDialogVisitActivities();
                                dialogo.setActivitySuccessListener(this);
                                dialogo.setArguments(bundle);
                                dialogo.show(fragmentManager, FragmentDialogVisitActivities.TAG);
                            }



                    }else{
                        Toast.makeText(context,getString(R.string.please_take_picture), Toast.LENGTH_SHORT) .show();
                    }
                }else{
                    getActivity().onBackPressed();
                    //Utilities.showErrorDialog(getActivity().getString(R.string.req_man_error_contacting_service), getActivity());
                }
                break;
            case R.id.btnCancel:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    public void takePhoto(){

        PackageManager packageManager = context.getPackageManager();
        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Toast.makeText(context, "This device does not have a camera.", Toast.LENGTH_SHORT) .show();
        }else {
                // create Intent to take a picture and return control to the calling application
            try {
                File photoFile = null;
                try {
                    photoFile = new File(nameFile);
                    photoFile.createNewFile();
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Necesita tener espacio libre en la memoria para completar esta accion", Toast.LENGTH_SHORT).show();
                }


                if (photoFile != null) {
                    mImageUri = Uri.fromFile(photoFile); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri); // set the image file name
                    startActivityForResult(intent, THUMBNAIL_ID);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == getActivity().RESULT_OK && requestCode == THUMBNAIL_ID) {
            image = getStorageImage(data);
            //image = data.getParcelableExtra("data");
            openDialog(image);

        }
    }

    public void openDialog(Bitmap bitmap){
        fragmentManager = getActivity().getSupportFragmentManager();
        FragmentDialogEvidence dialogo = new FragmentDialogEvidence();
        dialogo.setSetSetImgPhotoListener(this);
        dialogo.photoResult(bitmap);
        dialogo.show(fragmentManager, FragmentDialogEvidence.TAG);
    }

    @Override
    public void setImgPhoto(Bitmap bitmap) {
        image = bitmap;
        imgPhoto.setImageBitmap(image);
        imageEncode = encodeTobase64(image);
        Log.d(TAG,"Encoded: "+imageEncode);
    }

    @Override
    public void retry() {
        takePhoto();
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();

        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public Bitmap getStorageImage(Intent data)
    {
        Bitmap image = null;
        if (data == null) {

            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 5;
                options.inDither = true;

                Bitmap ima = BitmapFactory.decodeFile(nameFile,options);
                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                FileOutputStream fOut = new FileOutputStream(nameFile);

                ima.compress(Bitmap.CompressFormat.JPEG, 80, out2);
                ima.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
                ima.recycle();

                byte[] bytes =  out2.toByteArray();
                try {
                    out2.close();
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Bitmap unObjetoSerializable;
                unObjetoSerializable = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);


                image = unObjetoSerializable;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return image;
    }


    @Override
    public void getActivitySuccess(boolean success) {
        if(success){
            Toast.makeText(context,getString(R.string.finished_activity),Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }

    }
}
