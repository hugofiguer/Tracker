package util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sellcom.tracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Utilities {

    private Activity activity;
    public Utilities(){

    }

    public Utilities(Activity activity){
        this.activity = activity;
    }

    public static boolean isHandset(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void hideKeyboard(Context context, View editField) {

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editField.getWindowToken(), 0);
    }

    public static Bitmap stringToBitmap(String encodedString) {

        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public String getTypeStatus(String status){
        String typeStatus = "";
        switch (Integer.parseInt(status)){
            case 1: //Agendada
                typeStatus = activity.getString(R.string.status_scheduled);
                break;
            case 2: // Reagendada
                typeStatus = activity.getString(R.string.status_rescheduled);
                break;
            default:
                break;
        }
        return typeStatus;
    }

    public String getFormatDate(String fecha){
        long fecha_entero = Long.parseLong(fecha);
        String date = new SimpleDateFormat("dd/MM/yyyy")
                .format(new Date(fecha_entero * 1000L));
        return date;
    }

    public String getFormatTime(String fecha){
        long fecha_entero = Long.parseLong(fecha);
        String time =  new SimpleDateFormat("HH:mm")
                .format(new Date(fecha_entero * 1000L));
        return time;
    }

    //Mi mensaje de error..... PARA MI DIALOG
    //Si no hay internet y un servicio web no puede recuperarse este dialogo aparecera
    public static void showErrorDialog(String errorMessage, final Activity activity){
        Log.d("ShowErrorDialog", "Error message: " + errorMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
