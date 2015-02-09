package com.sellcom.tracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private int mIcon;
    private CharSequence mTitle;
    String CURRENT_FRAGMENT_TAG;
    Fragment fragment;
    public int depthCounter = 0;
    public boolean isDrawerOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mIcon = R.drawable.ic_launcher;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mNavigationDrawerFragment.selectItem(0);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String TAG;

        Fragment Aux = null;
        switch(position){
            case NavigationDrawerFragment.HOME:
                CURRENT_FRAGMENT_TAG = "home";
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null){
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                    Aux = new FragmentHome();
                }else
                    fragment = new FragmentHome();
                break;
            case NavigationDrawerFragment.WORK_PLAN:

                CURRENT_FRAGMENT_TAG = FragmentWorkPlan.TAG;
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null){
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                }else{
                    fragment = new FragmentWorkPlan();
                }
                break;
            default:
                return;
        }

        if (position > 0) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
            depthCounter = 1;
        } else if (position == 0) {
            depthCounter = 0;
        }

        if(fragmentManager.findFragmentByTag("home") != null)
            fragmentTransaction.remove(fragmentManager.findFragmentByTag("home"));

        if(Aux != null)
            fragment = Aux;

        fragmentTransaction.replace(R.id.container, fragment, CURRENT_FRAGMENT_TAG).commit();

    }

    public void onSectionAttached(int number) {
        Log.d("onSectionAttached/D", "Cambiando de icono...." + number);
        mTitle = getResources().getStringArray(R.array.drawer_titles)[number];
        mIcon  = getResources().obtainTypedArray(R.array.icons).getResourceId(number, R.drawable.ic_launcher);

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(mIcon);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        isDrawerOpen = mNavigationDrawerFragment.isDrawerOpen();
        if (!isDrawerOpen) {
            restoreActionBar();
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        try {
            String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+".photo";
            File dir = new File(file);
            if (dir.exists()) {
                File childFile[] = dir.listFiles();
                if (childFile != null) {
                    for (File file1 : childFile) {
                        file1.delete();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if (depthCounter == 1) {

            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment workplan  = fragmentManager.findFragmentByTag(FragmentWorkPlan.TAG);


            if(workplan != null && workplan.isAdded()){
                fragmentManager.beginTransaction().remove(workplan).commit();
                this.recreate();
            }

        } else {

            Fragment home = getSupportFragmentManager().findFragmentByTag("home");
            if(home != null && home.isAdded()){
                //Session.closeSession(getApplicationContext());
                moveTaskToBack(true);
            } else
                super.onBackPressed();
        }

        if (depthCounter > 0)
            depthCounter--;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == RESULT_OK) {
            FragmentManager         manager         = getSupportFragmentManager();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        FragmentManager     fragmentManager     = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    }



}
