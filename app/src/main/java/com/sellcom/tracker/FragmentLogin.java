package com.sellcom.tracker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import util.Utilities;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends Fragment implements View.OnClickListener{

    private Context context;
    private EditText txt_emailUser,txt_passwordUser;
    private Button boton;
    String          textEmail;
    String          textPassword;

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
        txt_passwordUser = (EditText)view.findViewById(R.id.passwordUser);

        return view;
    }


    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.sign_in_button){
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
            }else {
                Utilities.hideKeyboard(context, txt_passwordUser);
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        }

    }
}
