package com.sellcom.tracker_interno;


import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Locale;


public class FragmentSettings extends android.support.v4.app.Fragment implements AdapterView.OnItemSelectedListener {

    LinearLayout changeLanguage;
    Spinner languageSelector;
    boolean selectionFromUser;

    public static final String TAG = "settings";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        if (view != null) {

            changeLanguage = (LinearLayout) view.findViewById(R.id.change_language);
            languageSelector = (Spinner) view.findViewById(R.id.language_selector);

            selectionFromUser = false;
            String[] languages = new String[]{getString(R.string.spanish_label), getString(R.string.english_label)};
            ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, languages);
            languageSelector.setAdapter(languageAdapter);

            Locale current = getResources().getConfiguration().locale;

            if(String.valueOf(current).equals("en")) {
                languageSelector.setSelection(1);
            }

            languageSelector.setOnItemSelectedListener(this);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.SETTINGS);
    }

    public void changeLang(String lang) {
        if(lang.equals(""))
            return;

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (selectionFromUser) {
            String currentLanguage = (String) adapterView.getSelectedItem();
            if (currentLanguage.equals(getString(R.string.english_label)))
                changeLang("en");
            else
                changeLang("es");

            getActivity().recreate();
        }
        selectionFromUser = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
