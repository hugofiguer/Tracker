package com.sellcom.tracker;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import util.StepVisitAdapter;


public class FragmentStepVisit extends Fragment implements AdapterView.OnItemClickListener {

    final static public String TAG = "TAG_FRAGMENT_STEP_VISIT";
    private Context context;
    Fragment fragment;
    FragmentManager fragmentManager = getFragmentManager();
    private String id_visit;

    public FragmentStepVisit() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id_visit = getArguments().getString("id_visit");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_visit, container, false);

        if (view != null) {
            context = getActivity();
            OnInit(view);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void OnInit(View view) {
        GridView gridView = (GridView) view.findViewById(R.id.gridViewElements);
        gridView.setAdapter(new StepVisitAdapter(getActivity()));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
        switch(position){
            case 0:

                FragmentDialogVisitActivities dialogo = new FragmentDialogVisitActivities();
                dialogo.show(fragmentManager, FragmentDialogVisitActivities.TAG);
                ((MainActivity) getActivity()).depthCounter = 4;

                break;
            case 1:

                Bundle bundle = new Bundle();
                bundle.putString("id_visit",id_visit);

                fragment        = new FragmentVisitActivities();
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.container, fragment, FragmentVisitActivities.TAG);
                ((MainActivity) getActivity()).depthCounter = 4;

                break;

            case 2:

                Toast.makeText(context, "Posicion"+position, Toast.LENGTH_LONG).show();

                break;
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}
