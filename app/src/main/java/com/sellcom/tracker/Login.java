package com.sellcom.tracker;



import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import async_request.RequestManager;

public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RequestManager.sharedInstance().setActivity(this);

        FragmentLogin frag = new FragmentLogin();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.layoutLogin,frag,"LogIn");
        ft.commit();

    }
}
