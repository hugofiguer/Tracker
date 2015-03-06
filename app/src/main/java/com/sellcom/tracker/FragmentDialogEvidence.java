package com.sellcom.tracker;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


public class FragmentDialogEvidence extends DialogFragment implements View.OnClickListener{

    final static public String TAG = "TAG_FRAGMENT_DIALOG_EVIDENCE";
    private Button btnAccept, btnCancel, btnRetry;
    private ImageView imgEvidenceImage;
    setSetImgPhoto setSetImgPhoto;
    private Bitmap image;
    private Context context;

    public FragmentDialogEvidence() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

// safety check
        if (getDialog() == null) {
            return;
        }
        setCancelable(false); //evita que el dialog se cancele cuando presionas fuera de el
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //muestra el dialog en wrap_content

// ... other stuff you want to do in your onStart() method
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_dialog_evidence, container, false);

        imgEvidenceImage = (ImageView)view.findViewById(R.id.imgEvidenceImage);
        imgEvidenceImage.setImageBitmap(image);

        btnAccept = (Button)view.findViewById(R.id.btnAccept);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        btnRetry = (Button)view.findViewById(R.id.btnRetry);
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        return view;
    }

    public void photoResult(Bitmap data) {
        image = data;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnAccept:
                dismiss();
                setSetImgPhoto.setImgPhoto(image);//Regresa la imagen tomada a FragmentEvidence por medio de una interfaz
                break;
            case R.id.btnCancel:
                dismiss();
                break;
            case R.id.btnRetry:
                setSetImgPhoto.retry();
                dismiss();
                break;
        }

    }

    /*
    *
    * Interfaz creada para comunicacion con FragmentEvidence
     */
    public interface setSetImgPhoto{
        public void setImgPhoto(Bitmap bitmap);
        public void retry();
    }

    public void setSetSetImgPhotoListener(setSetImgPhoto listener) {
        setSetImgPhoto = listener;
    }


}
